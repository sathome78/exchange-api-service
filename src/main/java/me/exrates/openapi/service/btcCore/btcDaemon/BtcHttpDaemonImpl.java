package me.exrates.openapi.service.btcCore.btcDaemon;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.InstantSendListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Log4j2(topic = "bitcoin_core")
public class BtcHttpDaemonImpl implements BtcDaemon {

    private BtcdClient btcdClient;

    private BtcdDaemon daemon;

    public BtcHttpDaemonImpl(BtcdClient btcdClient) {
        this.btcdClient = btcdClient;
    }

    @Override
    public void init() {
        try {
            daemon = new BtcdDaemonImpl(btcdClient);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public Flux<Block> blockFlux(String port) {
        return Flux.push(emitter -> daemon.addBlockListener(new BlockListener() {
            @Override
            public void blockDetected(Block block) {
                log.debug(String.format("Block detected: hash %s, height %s ", block.getHash(), block.getHeight()));
                emitter.next(block);
            }
        }));
    }

    @Override
    public Flux<Transaction> walletFlux(String port) {
        return Flux.push(emitter ->  daemon.addWalletListener(new WalletListener() {
            @Override
            public void walletChanged(Transaction transaction) {
                log.debug(String.format("Wallet change: tx %s", transaction.getTxId()));
                emitter.next(transaction);
            }
        }));
    }

    @Override
    public Flux<Transaction> instantSendFlux(String port) {
        return Flux.push(emitter -> daemon.addInstantSendListener(new InstantSendListener() {
            @Override
            public void transactionBlocked(Transaction transaction) {
                log.debug(String.format("Transaction blocked: tx %s", transaction.getTxId()));
                emitter.next(transaction);
            }
        }));
    }

    @Override
    public void destroy() {
        daemon.removeAlertListeners();
        daemon.removeBlockListeners();
        daemon.removeWalletListeners();
        daemon.shutdown();
    }
}
