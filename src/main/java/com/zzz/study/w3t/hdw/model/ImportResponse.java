package com.zzz.study.w3t.hdw.model;


import lombok.Data;
import java.util.List;

@Data
public class ImportResponse {
    private boolean success;
    private String message;
    private List<AccountVO> data;

    // 快速构建成功响应
    public static ImportResponse success(List<AccountVO> accounts) {
        ImportResponse res = new ImportResponse();
        res.setSuccess(true);
        res.setMessage("扫描成功");
        res.setData(accounts);
        return res;
    }

    // 快速构建失败响应
    public static ImportResponse error(String msg) {
        ImportResponse res = new ImportResponse();
        res.setSuccess(false);
        res.setMessage(msg);
        return res;
    }
}