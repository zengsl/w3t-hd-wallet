package com.zzz.study.w3t.hdw.model;


import lombok.Data;

@Data
public class AssetVO {
    private String chain;     // 链名称，例如 "ETH", "BSC"
    private String symbol;    // 币种符号，例如 "Ethereum", "USDT"
    private String balance;   // 余额，例如 "2.5"
    private String usd;       // 美元估值，例如 "$4,500"
    private String icon;      // 前端图标类名，例如 "fa-brands fa-ethereum"
}