package com.zzz.study.w3t.hdw.model;

// 假设的实体类，如果不存在请创建，或者直接用 Map
public class WalletResult {
    String path;
    String address;
    String balance;

    public WalletResult(String path, String address, String balance) {
        this.path = path;
        this.address = address;
        this.balance = balance;
    }
}