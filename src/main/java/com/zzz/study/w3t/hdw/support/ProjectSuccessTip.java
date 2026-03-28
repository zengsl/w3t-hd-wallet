package com.zzz.study.w3t.hdw.support;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author zengsl
 */
@Component
@Order(1) // 如果有多个Runner，可以用Order控制执行顺序，数字越小越靠前
public class ProjectSuccessTip implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 这里可以直接使用 System.out.println 或者日志
        // \n 用于换行，保持格式美观
        String successBanner = """
                
                ==================================
                   🎉 项目启动成功！\s
                   祝你今天工作顺利，无BUG~
                ==================================""";
        System.out.println(successBanner);
    }
}