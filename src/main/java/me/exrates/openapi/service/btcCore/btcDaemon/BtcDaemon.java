package me.exrates.openapi.service.btcCore.btcDaemon;

public interface BtcDaemon {


    void init();

    void destroy();

    Flux<Block> blockFlux(String port);

    Flux<Transaction> walletFlux(String port);

    Flux<Transaction> instantSendFlux(String port);
}
