package me.exrates.model.dto.merchants.neo;

public enum NeoAsset {
    NEO("NEO", "0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b"),
    GAS("GAS", "0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7"),
    KAZE("KAZE", "0xf1fee7945e5ba7fed56272b916094ed8f384a94e63d5f8d81214dfde489ffb17"),
    STREAM("STRM", "0x9d2593e23db5e8946969a16d1980ee57e04bb25904bb0ca0d181257eb48a5398");

    private String name;
    private String id;

    NeoAsset(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
