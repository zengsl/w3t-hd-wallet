package com.zzz.study.w3t.hdw.enums;

public enum CoinType {
    BITCOIN(0, "BTC", "Bitcoin", "MainNet"),
    BITCOIN_TESTNET(1, "tBTC", "Bitcoin Testnet", "TestNet"),
    ETHEREUM(60, "ETH", "Ethereum", "MainNet"),
    ETHEREUM_SEPOLIA(11155111, "ETH", "Ethereum Sepolia", "TestNet"),
    LITECOIN(2, "LTC", "Litecoin", "MainNet");

    private final int coinType;
    private final String symbol;
    private final String name;
    private final String networkName;

    CoinType(int coinType, String symbol, String name, String networkName) {
        this.coinType = coinType;
        this.symbol = symbol;
        this.name = name;
        this.networkName = networkName;
    }

    public int getCoinType() {
        return coinType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getNetworkName() {
        return networkName;
    }

    // 根据 coinType 数字查找枚举
    public static CoinType fromCoinType(int coinType) {
        for (CoinType coin : CoinType.values()) {
            if (coin.coinType == coinType) {
                return coin;
            }
        }
        throw new IllegalArgumentException("Unknown coin type: " + coinType);
    }
}