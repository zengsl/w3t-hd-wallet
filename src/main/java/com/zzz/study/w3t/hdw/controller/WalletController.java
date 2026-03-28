package com.zzz.study.w3t.hdw.controller;

import com.zzz.study.w3t.hdw.service.strategy.LocalTransactionStrategy;
import com.zzz.study.w3t.hdw.service.strategy.MpcTransactionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/eth")
public class WalletController {

    @Autowired
    private LocalTransactionStrategy localStrategy;

    @Autowired
    private MpcTransactionStrategy mpcStrategy;

    @GetMapping("/transfer")
    public String showTransferPage() {
        return "eth-transfer";
    }

    /**
     * 处理本地私钥签名交易
     */
    @PostMapping("/transfer/local")
    public String transferLocal(@RequestParam String privateKey,
                                @RequestParam String from,
                                @RequestParam String to,
                                @RequestParam BigDecimal amount,
                                Model model) {
        try {
            Map<String, Object> result = localStrategy.send(from, to, amount, privateKey);
            model.addAttribute("result", result);
            return "eth-result";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mode", "本地签名");
            return "eth-transfer";
        }
    }

    /**
     * 处理 MPC 签名交易（不需要私钥）
     */
    @PostMapping("/transfer/mpc")
    public String transferMpc(@RequestParam String from,
                              @RequestParam String to,
                              @RequestParam BigDecimal amount,
                              Model model) {
        try {
            // MPC 模式下，privateKey 传 null 或空字符串
            Map<String, Object> result = mpcStrategy.send(from, to, amount, null);
            model.addAttribute("result", result);
            return "eth-result";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mode", "MPC签名");
            return "eth-transfer";
        }
    }
}