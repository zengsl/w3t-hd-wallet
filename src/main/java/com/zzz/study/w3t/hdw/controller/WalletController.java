package com.zzz.study.w3t.hdw.controller;

import com.zzz.study.w3t.hdw.service.MpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class WalletController {

    @Autowired
    private MpcService mpcService;

    // 1. 处理根目录访问，重定向到转账页面
    @GetMapping("/")
    public String index() {
        // 直接跳转到功能页面，避免空白页
        return "redirect:/eth/transfer";
    }

    // 2. 转账页面
    @GetMapping("/eth/transfer")
    public String showTransferPage() {
        // 对应 templates/eth-transfer.html
        return "eth-transfer";
    }

    // 3. 处理转账请求
    @PostMapping("/eth/transfer")
    public String transfer(@RequestParam String privateKey,
                           @RequestParam String toAddress,
                           @RequestParam BigDecimal amount,
                           Model model) {
        try {
            Map<String, Object> result = mpcService.signAndSendTransaction(privateKey, toAddress, amount);
            model.addAttribute("result", result);
            return "eth-result";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "eth-transfer";
        }
    }
}