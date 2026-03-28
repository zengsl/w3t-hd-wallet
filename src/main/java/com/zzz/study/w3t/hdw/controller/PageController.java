package com.zzz.study.w3t.hdw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    /**
     * 首页映射
     * 访问 http://localhost:8080/ 显示 index.html
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 转账页面映射
     * 访问 http://localhost:8080/eth/transfer 显示 eth-transfer.html
     */
    @GetMapping("/eth/transfer")
    public String showTransferPage() {
        return "eth-transfer";
    }

    @GetMapping("/import")
    public String importMnemonic() {
        return "import";
    }
}