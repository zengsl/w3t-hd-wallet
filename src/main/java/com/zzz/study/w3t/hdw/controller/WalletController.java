package com.zzz.study.w3t.hdw.controller;

import com.zzz.study.w3t.hdw.model.AccountVO;
import com.zzz.study.w3t.hdw.model.ImportResponse;
import com.zzz.study.w3t.hdw.service.ScannerService;
import com.zzz.study.w3t.hdw.service.strategy.LocalTransactionStrategy;
import com.zzz.study.w3t.hdw.service.strategy.MpcTransactionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 注意：这里去掉了 @Controller，或者保留 @Controller 并在方法上加 @ResponseBody
// 或者直接使用 @RestController
@RestController
@RequestMapping
public class WalletController {

    @Autowired
    private ScannerService scannerService;

    @Autowired
    private LocalTransactionStrategy localStrategy;

    @Autowired
    private MpcTransactionStrategy mpcStrategy;

    // 注意：因为用了 @RestController，原来的 HTML 页面需要单独映射
    // 或者你也可以保留 @Controller 并只在 API 方法上加 @ResponseBody
    // 下面的代码假设你只用这个类处理 API
    // 如果你混合使用，请参考下方的备选方案

    /**
     * 处理本地私钥签名交易 (API)
     */
    @PostMapping("/transfer/local")
    // 如果你没用 @RestController，请在这里加上 @ResponseBody
    public Map<String, Object> transferLocal(@RequestBody Map<String, String> payload) {
        String privateKey = payload.get("privateKey");
        String from = payload.get("from");
        String to = payload.get("to");
        BigDecimal amount = new BigDecimal(payload.get("amount"));

        try {
            Map<String, Object> result = localStrategy.send(from, to, amount, privateKey);
            // 统一返回格式
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("txHash", result.get("txHash"));
            response.put("message", "交易成功");
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", e.getMessage());
            return error;
        }
    }

    /**
     * 处理 MPC 签名交易 (API)
     */
    @PostMapping("/transfer/mpc")
    public Map<String, Object> transferMpc(@RequestBody Map<String, String> payload) {
        String from = payload.get("from");
        String to = payload.get("to");
        BigDecimal amount = new BigDecimal(payload.get("amount"));

        try {
            Map<String, Object> result = mpcStrategy.send(from, to, amount, null);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("txHash", result.get("txHash"));
            response.put("message", "交易成功");
            return response;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", e.getMessage());
            return error;
        }
    }

    // API: 接收助记词并返回扫描结果
    @PostMapping("/api/scan")
    @ResponseBody
    public ImportResponse scanWallet(@RequestBody Map<String, String> request) {
        String mnemonic = request.get("mnemonic");

        try {
            // 1. 调用 Service 层进行真实扫描
            // 这里假设你的 Service 已经写好了，返回的是 AccountVO 列表
            List<AccountVO> scannedAccounts = scannerService.scanAndGroupAssets(mnemonic);

            // 2. 返回统一格式
            return ImportResponse.success(scannedAccounts);

        } catch (Exception e) {
            e.printStackTrace();
            return ImportResponse.error("导入失败: " + e.getMessage());
        }
    }
}