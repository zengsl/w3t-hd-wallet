package com.zzz.study.w3t.hdw.service.strategy;

import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocalTransactionStrategy implements TransactionStrategy {

    private final Web3j web3j;

    public LocalTransactionStrategy(Web3j web3j) {
        this.web3j = web3j;
    }

    @Override
    public Map<String, Object> send(String from, String to, BigDecimal amount, String privateKey) {
        Map<String, Object> result = new HashMap<>();
        try {
            Credentials credentials = Credentials.create(privateKey);
            if (!credentials.getAddress().equalsIgnoreCase(from)) {
                throw new IllegalArgumentException("私钥与发送地址不匹配");
            }

            var txReceipt = Transfer.sendFunds(web3j, credentials, to, amount, Convert.Unit.ETHER).send();

            result.put("txHash", txReceipt.getTransactionHash());
            result.put("type", "Local (Private Key)");
            result.put("status", "SUCCESS");
        } catch (Exception e) {
            throw new RuntimeException("本地签名失败: " + e.getMessage());
        }
        return result;
    }
}