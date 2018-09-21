package me.exrates.openapi.service.btcCore.btcDaemon;

import lombok.extern.log4j.Log4j2;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Log4j2(topic = "bitcoin_core")
public class BtcdZMQDaemonImpl implements BtcDaemon{


    private volatile boolean isActive = false;

    private BtcdClient btcdClient;
    private Context zmqContext;

    private ExecutorService listenerThreadPool = Executors.newFixedThreadPool(3);
    private static Predicate<String> hexStringChecker = Pattern.compile("^[a-fA-F0-9]+$").asPredicate();

    public BtcdZMQDaemonImpl(BtcdClient btcdClient, Context zmqContext) {
        this.btcdClient = btcdClient;
        this.zmqContext = zmqContext;
    }

    @Override
    public void init() {
        if (btcdClient == null) {
            throw new BitcoinCoreException("Client not initialized!");
        }
        if (zmqContext == null) {
            throw new BitcoinCoreException("ZmQ context not initialized!");
        }
        isActive = true;
    }

    private void initSubscriber(String port, String topic, Consumer<String> onNext,
                                Consumer<Throwable> onError,
                                Consumer<String> onCompleted) {
        String host = btcdClient.getNodeConfig().getProperty("node.bitcoind.rpc.host");
        String address = String.join( "","tcp://", host, ":", port);
        log.info("Subscribing {} listener on address {} ", topic, address);

        try (Socket subscriber = zmqContext.socket(ZMQ.SUB)) {
            if (port != null) {
                if (subscriber.connect(address)) {
                    subscriber.subscribe(topic);
                    ZMQ.Poller poller = zmqContext.poller(1);
                    poller.register(subscriber, ZMQ.Poller.POLLIN);
                    log.info("Successfully subscribed {} listener on port {} ", topic, port);
                    while (isActive) {
                        poller.poll(5000);
                        if (poller.pollin(0)) {
                            try {
                                String hex = extractMessage(subscriber);
                                log.debug("socket {} got notification {} ", port, hex);
                                if (hexStringChecker.test(hex)) {
                                    onNext.accept(hex);
                                } else {
                                    log.warn("Illegal notification format: {}", hex);
                                }
                            } catch (Exception e) {
                                log.error(e);
                                if (!isActive) {
                                    onError.accept(e);
                                }
                            }
                        }
                    }
                    onCompleted.accept("");
                } else {
                    onError.accept(new BitcoinCoreException("Could not connect to port " + port));
                }
            }
        } catch (Exception e) {
            onError.accept(e);
        }
    }



    private String extractMessage(Socket subscriber) {
        List<byte[]> multipartMessage = new ArrayList<>();
        byte[] message = subscriber.recv(1);
        multipartMessage.add(message);
        while (subscriber.hasReceiveMore()) {
            multipartMessage.add(subscriber.recv(1));
        }
        if (multipartMessage.size() >= 2) {
            return DatatypeConverter.printHexBinary(multipartMessage.get(1)).toLowerCase();
        } else {
            return "";
        }
    }

    @Override
    public void destroy() {
        isActive = false;
        zmqContext.close();
        zmqContext.term();
       listenerThreadPool.shutdown();
    }

    @Override
    public Flux<Block> blockFlux(String port) {
        return notificationFlux(port, "hashblock", this::getBlock);
    }

    @Override
    public Flux<Transaction> walletFlux(String port) {
        return notificationFlux(port, "hashtx", this::getTransaction);
    }

    @Override
    public Flux<Transaction> instantSendFlux(String port) {
        return notificationFlux(port, "hashtxlock", this::getTransaction);
    }

    private <T> Flux<T> notificationFlux(String port, String topic, Function<String, T> mapper) {
        Flux<String> source = Flux.push(emitter -> initSubscriber(port, topic,
                emitter::next, emitter::error, (v) -> emitter.complete()));
        return source.subscribeOn(Schedulers.fromExecutorService(listenerThreadPool))
                .flatMap(hex -> {
            try {
                T resultItem = mapper.apply(hex);
                return Flux.just(resultItem);
            } catch (Exception e) {
                return Flux.empty();
            }
        });
    }

    private Block getBlock(String blockhash) {
        try {
            return btcdClient.getBlock(blockhash);
        } catch (Exception e) {
            log.error(e);
            throw new BitcoinCoreException(e.getMessage());
        }
    }

    private Transaction getTransaction(String txId) {
        try {
            return btcdClient.getTransaction(txId);
        }

        catch (Exception e) {
            throw new BitcoinCoreException(e.getMessage());
        }
    }



}
