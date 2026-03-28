package com.zzz.study.w3t.hdw.model;

import lombok.Data;
import java.util.List;

@Data
public class AccountVO {
    private String accountName;   // 例如: "账户 1 (m/44'/60'/0'/0/0)"
    private String address;       // 账户的主地址
    private String totalUsd;      // 账户总估值，例如 "$12,450.00"
    private List<AssetVO> assets; // 该账户下的所有链上资产
}