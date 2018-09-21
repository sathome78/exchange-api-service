package me.exrates.openapi.service.btcCore;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.service.btcCore.btcDaemon.BtcDaemon;
import me.exrates.openapi.service.btcCore.btcDaemon.BtcHttpDaemonImpl;
import me.exrates.openapi.service.btcCore.btcDaemon.BtcdZMQDaemonImpl;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
@Log4j2(topic = "bitcoin_core")
public class CoreWalletService {

    private static final int KEY_POOL_LOW_THRESHOLD = 10;
    private static final int MIN_CONFIRMATIONS_FOR_SPENDING = 3;

    @Autowired
    private ZMQ.Context zmqContext;


    private BtcdClient btcdClient;

    private BtcDaemon btcDaemon;

    private Boolean supportInstantSend;
    private Boolean supportSubtractFee;
    private Boolean supportReferenceLine;

    private Map<String, ScheduledFuture<?>> unlockingTasks = new ConcurrentHashMap<>();

    private ScheduledExecutorService outputUnlockingExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Object SENDING_LOCK = new Object();


    @Override
    public void initCoreClient(String nodePropertySource, boolean supportInstantSend, boolean supportSubtractFee, boolean supportReferenceLine) {
        try {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
                    .build();
            Properties nodeConfig = new Properties();
            nodeConfig.load(getClass().getClassLoader().getResourceAsStream(nodePropertySource));
            log.info("Node config: " + nodeConfig);
            btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
            this.supportInstantSend = supportInstantSend;
            this.supportSubtractFee = supportSubtractFee;
            this.supportReferenceLine = supportReferenceLine;
        } catch (Exception e) {
            log.error("Could not initialize BTCD client of config {}. Reason: {} ", nodePropertySource, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

    }

    @Override
    public void initBtcdDaemon(boolean zmqEnabled) {
        if (zmqEnabled) {
            btcDaemon = new BtcdZMQDaemonImpl(btcdClient, zmqContext);
        } else {
            btcDaemon = new BtcHttpDaemonImpl(btcdClient);
        }

        try {
            btcDaemon.init();
        } catch (Exception e) {
            log.error(e);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    @Override
    public String getNewAddress(String walletPassword) {
        Integer keyPoolSize = getKeypoolSize();

        try {

            /*
             * If wallet is encrypted and locked, pool of private keys is not refilled
             * Keys are automatically refilled on unlocking
             * */
            if (keyPoolSize < KEY_POOL_LOW_THRESHOLD) {
                unlockWallet(walletPassword, 1);
            }
            return btcdClient.getNewAddress();
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            throw new BitcoinCoreException("Cannot generate new address!");
        }
    }

    private Integer getKeypoolSize() {
        Integer keyPoolSize;
        try {
            keyPoolSize = btcdClient.getInfo().getKeypoolSize();
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            try {
                keyPoolSize = btcdClient.getWalletInfo().getKeypoolSize();
            } catch (BitcoindException | CommunicationException e2) {
                log.error(e2);
                throw new BitcoinCoreException("Cannot generate new address!");
            }
        }
        return keyPoolSize;
    }

    @Override
    public void backupWallet(String backupFolder) {
        try {
            String filename = new StringJoiner("").add(backupFolder).add("backup_")
                    .add((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))))
                    .add(".dat").toString();
            log.debug("Backing up wallet to file: " + filename);
            btcdClient.backupWallet(filename);
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
        }
    }

    @Override
    public void shutdown() {
        btcdClient.close();
        btcDaemon.destroy();
    }


    private Optional<Transaction> handleConflicts(Transaction transaction) {
        if (transaction.getConfirmations() < 0 && !transaction.getWalletConflicts().isEmpty()) {
            log.warn("Wallet conflicts present");
            for (String txId : transaction.getWalletConflicts()) {
                try {
                    Transaction conflicted = btcdClient.getTransaction(txId);
                    if (conflicted.getConfirmations() >= 0) {
                        return Optional.of(conflicted);
                    }
                } catch (BitcoindException | CommunicationException e) {
                    log.error(e);
                }
            }
            return Optional.empty();
        } else {
            return Optional.of(transaction);
        }
    }

    @Override
    public Optional<BtcTransactionDto> handleTransactionConflicts(String txId) {
        try {
            log.debug(this);
            return handleConflicts(btcdClient.getTransaction(txId)).map(this::convert);
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    private BtcTransactionDto convert(Transaction tx) {
        List<BtcTxPaymentDto> payments = tx.getDetails().stream()
                .map((payment) -> new BtcTxPaymentDto(payment.getAddress(), payment.getCategory().getName(), payment.getAmount(), payment.getFee()))
                .collect(Collectors.toList());
        return new BtcTransactionDto(tx.getAmount(), tx.getFee(), tx.getConfirmations(), tx.getTxId(), tx.getBlockHash(), tx.getWalletConflicts(), tx.getTime(),
                tx.getTimeReceived(), tx.getComment(), tx.getTo(), payments);
    }

    @Override
    public BtcTransactionDto getTransaction(String txId) {
        try {
            Transaction tx = btcdClient.getTransaction(txId);
            return convert(tx);
        } catch (BitcoindException | CommunicationException e) {
            throw new BitcoinCoreException(e.getMessage());
        }
    }


    @Override
    public BtcWalletInfoDto getWalletInfo() {
        BtcWalletInfoDto dto = new BtcWalletInfoDto();

        try {
            BigDecimal spendableBalance = btcdClient.getBalance("", MIN_CONFIRMATIONS_FOR_SPENDING);
            dto.setBalance(BigDecimalProcessing.formatNonePoint(spendableBalance, true));

            WalletInfo walletInfo = btcdClient.getWalletInfo();

            BigDecimal confirmedNonSpendableBalance = BigDecimalProcessing.doAction(walletInfo.getBalance(), spendableBalance, ActionType.SUBTRACT);
            BigDecimal unconfirmedBalance = btcdClient.getUnconfirmedBalance();
            dto.setConfirmedNonSpendableBalance(BigDecimalProcessing.formatNonePoint(confirmedNonSpendableBalance, true));
            dto.setUnconfirmedBalance(BigDecimalProcessing.formatNonePoint(unconfirmedBalance, true));
            dto.setTransactionCount(walletInfo.getTxCount());
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            try {
                BigDecimal spendableBalance = btcdClient.getBalance();
                dto.setBalance(BigDecimalProcessing.formatNonePoint(spendableBalance, true));
            } catch (BitcoindException | CommunicationException e1) {
                log.error(e1);
            }
        }
        return dto;

    }

    @Override
    public List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations) {
        try {
            List<Address> received = btcdClient.listReceivedByAddress(minConfirmations);
            return received.stream().flatMap(address -> address.getTxIds().stream().map(txId -> {
                TxReceivedByAddressFlatDto dto = new TxReceivedByAddressFlatDto();
                dto.setAccount(address.getAccount());
                dto.setAddress(address.getAddress());
                dto.setAmount(address.getAmount());
                dto.setConfirmations(address.getConfirmations());
                dto.setTxId(txId);
                return dto;
            })).collect(Collectors.toList());
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            throw new BitcoinCoreException(e.getMessage());
        }
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        try {
            return btcdClient.listSinceBlock().getPayments().stream()
                    .map(payment -> {
                        BtcTransactionHistoryDto dto = new BtcTransactionHistoryDto();
                        dto.setTxId(payment.getTxId());
                        dto.setAddress(payment.getAddress());
                        dto.setBlockhash(payment.getBlockHash());
                        dto.setCategory(payment.getCategory().getName());
                        dto.setAmount(BigDecimalProcessing.formatNonePoint(payment.getAmount(), true));
                        dto.setFee(BigDecimalProcessing.formatNonePoint(payment.getFee(), true));
                        dto.setConfirmations(payment.getConfirmations());
                        dto.setTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(payment.getTime()), ZoneId.systemDefault()));
                        return dto;
                    }).collect(Collectors.toList());
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            throw new BitcoinCoreException(e.getMessage());
        }
    }


    @Override
    public List<BtcPaymentFlatDto> listSinceBlockEx(@Nullable String blockHash, Integer merchantId, Integer currencyId) {
        try {
            return listSinceBlockExChecked(blockHash, merchantId, currencyId);
        } catch (Exception e) {
            log.error(e);
            throw new BitcoinCoreException(e);
        }
    }

    private List<BtcPaymentFlatDto> listSinceBlockExChecked(@Nullable String blockHash, Integer merchantId, Integer currencyId) throws BitcoindException, CommunicationException {
        SinceBlock sinceBlock = blockHash == null ? btcdClient.listSinceBlock() : btcdClient.listSinceBlock(blockHash);
        return sinceBlock.getPayments().stream()
                .map(payment -> BtcPaymentFlatDto.builder()
                        .amount(payment.getAmount())
                        .confirmations(payment.getConfirmations())
                        .merchantId(merchantId)
                        .currencyId(currencyId)
                        .address(payment.getAddress())
                        .txId(payment.getTxId())
                        .blockhash(payment.getBlockHash())
                        .build()).collect(Collectors.toList());
    }


    @Override
    public List<BtcPaymentFlatDto> listSinceBlock(@Nullable String blockHash, Integer merchantId, Integer currencyId) {
        try {
            return listSinceBlockExChecked(blockHash, merchantId, currencyId);
        } catch (Exception e) {
            log.error(e);
            return Collections.emptyList();
        }
    }


    @Override
    public BigDecimal estimateFee(int blockCount) {
        try {
            return btcdClient.estimateFee(blockCount);
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            try {
                SmartFee smartFee = btcdClient.estimateSmartFee(blockCount);
                if (smartFee.getErrors() != null && !smartFee.getErrors().isEmpty()) {
                    return BigDecimal.valueOf(-1L);
                } else {
                    return smartFee.getFeeRate();
                }
            } catch (BitcoindException | CommunicationException e1) {
                log.error(e1);
            }
            return new BigDecimal(-1L);
        }
    }

    @Override
    public BigDecimal getActualFee() {
        try {
            return btcdClient.getInfo().getPayTxFee();

        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            try {
                return btcdClient.getWalletInfo().getPayTxFee();
            } catch (BitcoindException | CommunicationException e1) {
                log.error(e1);
            }
            throw new BitcoinCoreException(e);
        }

    }

    @Override
    public void setTxFee(BigDecimal fee) {
        try {
            btcdClient.setTxFee(fee);
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            throw new BitcoinCoreException(e.getMessage());
        }
    }

    @Override
    public void submitWalletPassword(String password) {
        try {
            unlockWallet(password, 60);
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            throw new BitcoinCoreException(e.getMessage());
        }
    }


    /*
     * Using sendMany instead of sendToAddress allows to send only UTXO with certain number of confirmations.
     * DO NOT use immutable map creation methods like Collections.singletonMap(...), it will cause an error within lib code
     * */
    @Override
    public String sendToAddressAuto(String address, BigDecimal amount, String walletPassword) {

        try {
            String result;
            synchronized (SENDING_LOCK) {
                unlockWallet(walletPassword, 10);
                Map<String, BigDecimal> payments = new HashMap<>();
                payments.put(address, amount);
                if (supportReferenceLine) {
                    result = btcdClient.sendMany("", payments, "", MIN_CONFIRMATIONS_FOR_SPENDING);
                } else {
                    result = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING);
                }
                lockWallet();
            }
            return result;
        } catch (BitcoindException e) {
            log.error(e);
            if (e.getCode() == -5) {
                throw new InvalidAccountException();
            }
            if (e.getCode() == -6) {
                throw new InsufficientCostsInWalletException();
            }
            throw new MerchantException(e.getMessage());
        } catch (CommunicationException e) {
            log.error(e);
            throw new MerchantException(e.getMessage());
        }
    }

    private void unlockWallet(String password, int authTimeout) throws BitcoindException, CommunicationException {
        unlockWallet(password, authTimeout, false);
    }

    private void forceUnlockWallet(String password, int authTimeout) throws BitcoindException, CommunicationException {
        unlockWallet(password, authTimeout, true);
    }

    private void unlockWallet(String password, int authTimeout, boolean forceUnlock) throws BitcoindException, CommunicationException {
        Long unlockedUntil = getUnlockedUntil();
        if (unlockedUntil != null && (forceUnlock || unlockedUntil == 0)) {
            btcdClient.walletPassphrase(password, authTimeout);
        }
    }

    private void lockWallet() {
        try {
            Long unlockedUntil = getUnlockedUntil();
            if (unlockedUntil != null && unlockedUntil != 0) {
                btcdClient.walletLock();
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private Long getUnlockedUntil() throws BitcoindException, CommunicationException {
        try {
            return btcdClient.getInfo().getUnlockedUntil();
        } catch (Exception e) {
            log.error(e);
            return btcdClient.getWalletInfo().getUnlockedUntil();
        }
    }

    @Override
    public BtcPaymentResultDto sendToMany(Map<String, BigDecimal> payments, boolean subtractFeeFromAmount) {
        try {
            List<String> subtractFeeAddresses = new ArrayList<>();
            if (subtractFeeFromAmount) {
                subtractFeeAddresses = new ArrayList<>(payments.keySet());
            }
            String txId;
            synchronized (SENDING_LOCK) {
                if (supportInstantSend) {
                    txId = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING, false,
                            "", subtractFeeAddresses);
                } else if (supportReferenceLine) {
                    txId = btcdClient.sendMany("", payments, "", MIN_CONFIRMATIONS_FOR_SPENDING);
                } else {
                    if (supportSubtractFee) {
                        txId = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING,
                                "", subtractFeeAddresses);
                    } else {
                        txId = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING, "");
                    }
                }
            }
            return new BtcPaymentResultDto(txId);
        } catch (Exception e) {
            log.error(e);
            return new BtcPaymentResultDto(e);
        }
    }

    @Override
    public Flux<BtcBlockDto> blockFlux() {
        return notificationFlux("node.bitcoind.notification.block.port", btcDaemon::blockFlux, block ->
                new BtcBlockDto(block.getHash(), block.getHeight(), block.getTime()));
    }

    @Override
    public Flux<BtcTransactionDto> walletFlux() {
        return notificationFlux("node.bitcoind.notification.wallet.port", btcDaemon::walletFlux, this::convert);
    }

    @Override
    public Flux<BtcTransactionDto> instantSendFlux() {
        return notificationFlux("node.bitcoind.notification.instantsend.port", btcDaemon::instantSendFlux, this::convert);
    }

    private <S, T> Flux<T> notificationFlux(String portProperty, Function<String, Flux<S>> source, Function<S, T> mapper) {
        if (btcdClient != null) {
            String port = btcdClient.getNodeConfig().getProperty(portProperty);
            return source.apply(port).map(mapper);
        } else {
            log.error("Client not initialized!");
            return Flux.empty();
        }
    }


    @Override
    public BtcPreparedTransactionDto prepareRawTransaction(Map<String, BigDecimal> payments) {
        return prepareRawTransaction(payments, null);
    }

    @Override
    public BtcPreparedTransactionDto prepareRawTransaction(Map<String, BigDecimal> payments, @Nullable String oldTxHex) {
        try {

            FundingResult fundingResult;
            synchronized (SENDING_LOCK) {
                if (oldTxHex != null && unlockingTasks.containsKey(oldTxHex)) {

                    unlockingTasks.remove(oldTxHex).cancel(true);
                    // unlock previously locked UTXO

                    lockUnspentFromHex(oldTxHex, true);

                }

                String initialTxHex = btcdClient.createRawTransaction(new ArrayList<>(), payments);
                fundingResult = btcdClient.fundRawTransaction(initialTxHex);
                lockUnspentFromHex(fundingResult.getHex(), false);
            }

            unlockingTasks.put(fundingResult.getHex(), outputUnlockingExecutor.schedule(() -> {
                        // unlock UTXO after 2 minutes - in case of no action;
                        lockUnspentFromHex(fundingResult.getHex(), true);
                        log.info("Outputs unlocked for hex " + fundingResult.getHex());
                        unlockingTasks.remove(fundingResult.getHex());
                    },
                    2, TimeUnit.MINUTES));
            ;

            return new BtcPreparedTransactionDto(payments, fundingResult.getFee(), fundingResult.getHex());
        } catch (BitcoindException | CommunicationException e) {
            throw new BitcoinCoreException(e);
        }
    }


    private void lockUnspentFromHex(String hex, boolean unlock) {
        try {
            RawTransactionOverview txOverview = btcdClient.decodeRawTransaction(hex);
            btcdClient.lockUnspent(unlock, txOverview.getVIn().stream()
                    .map(vin -> new OutputOverview(vin.getTxId(), vin.getVOut())).collect(Collectors.toList()));
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
        }
    }

    @Override
    public BtcPaymentResultDto signAndSendRawTransaction(String hex) {
        try {
            checkLockStateForRawTx(hex);
            SignatureResult signatureResult = btcdClient.signRawTransaction(hex);
            if (!signatureResult.getComplete()) {
                throw new BitcoinCoreException("Signature failed!");
            }
            String txId = btcdClient.sendRawTransaction(signatureResult.getHex(), false);
            return new BtcPaymentResultDto(txId);
        } catch (Exception e) {
            log.error(e);
            return new BtcPaymentResultDto(e);
        }
    }


    private void checkLockStateForRawTx(String hex) {
        try {
            Set<BtcTxOutputDto> lockedOutputSet = btcdClient.listLockUnspent().stream()
                    .map(out -> new BtcTxOutputDto(out.getTxId(), out.getVOut())).collect(Collectors.toSet());

            boolean txOutputsLocked = btcdClient.decodeRawTransaction(hex).getVIn().stream()
                    .map(vin -> new BtcTxOutputDto(vin.getTxId(), vin.getVOut())).allMatch(lockedOutputSet::contains);

            if (!txOutputsLocked) {
                throw new IllegalStateException("Transaction outputs already unlocked!");
            }

        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
        }
    }

    @Override
    public String getTxIdByHex(String hex) {
        try {
            return btcdClient.decodeRawTransaction(hex).getTxId();
        } catch (BitcoindException | CommunicationException e) {
            throw new BitcoinCoreException("Cannot decode tx " + hex, e);
        }
    }

    @Override
    public String getLastBlockHash() {
        try {
            return btcdClient.getBestBlockHash();
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            try {
                return btcdClient.getBlockHash(btcdClient.getBlockCount());
            } catch (BitcoindException | CommunicationException e1) {
                log.error(e1);
            }
            throw new BitcoinCoreException(e);
        }
    }

    @Override
    public BtcBlockDto getBlockByHash(String blockHash) {
        try {
            Block block = btcdClient.getBlock(blockHash);
            return new BtcBlockDto(block.getHash(), block.getHeight(), block.getTime());
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
            throw new BitcoinCoreException(e);
        }
    }

    @PreDestroy
    private void shutDown() {
        outputUnlockingExecutor.shutdown();
    }


}
