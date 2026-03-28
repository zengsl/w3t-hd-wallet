package com.zzz.study.w3t.hdw.service.strategy;

import java.math.BigDecimal;
import java.util.Map;

public interface TransactionStrategy {

    Map<String, Object> send(String from, String to, BigDecimal amount, String privateKey);

}
