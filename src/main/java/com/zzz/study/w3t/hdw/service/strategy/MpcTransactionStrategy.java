package com.zzz.study.w3t.hdw.service.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class MpcTransactionStrategy implements TransactionStrategy {

    @Override
    public Map<String, Object> send(String from, String to, BigDecimal amount, String privateKey) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 模拟后端 MPC 签名逻辑
            System.out.println(">>> [MPC模式] 正在后端查找地址 " + from + " 的密钥分片...");

            // 模拟处理延迟
            Thread.sleep(500);

            result.put("txHash", "0xMPC_" + System.currentTimeMillis() + "_" + from.substring(0, 5));
            result.put("type", "MPC (Server Side)");
            result.put("status", "SUCCESS");

        } catch (Exception e) {
            throw new RuntimeException("MPC 签名失败: " + e.getMessage());
        }
        return result;
    }
}