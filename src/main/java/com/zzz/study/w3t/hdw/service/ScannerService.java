package com.zzz.study.w3t.hdw.service;


import com.zzz.study.w3t.hdw.enums.CoinType;
import com.zzz.study.w3t.hdw.model.AccountVO;
import com.zzz.study.w3t.hdw.model.AssetVO;
import com.zzz.study.w3t.hdw.utils.HdWalletUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.crypto.DeterministicKey;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * @author zengsl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScannerService {

    public static final int MAX_EMPTY_GAP = 20;

    public final EthService ethService;

    /**
     * 扫描逻辑：接收助记词，派生前20个地址，探测余额并组装成账户结构
     */
    public List<AccountVO> scanAndGroupAssets(String mnemonic) {
        List<AccountVO> accountList = new ArrayList<>();

        // --- 真实业务逻辑开始 ---

        // 1. 生成根密钥 (请替换为你项目里的实际代码)
        // RootKey rootKey = HDKeyUtil.generateRootKey(mnemonic);
        DeterministicKey masterPrivateKey = HdWalletUtil.getMasterPrivateKey(mnemonic);
        // 2. 循环派生地址 (这里演示扫描前 5 个，实际可以改成 20 或更多)
        for (int i = 0; i < 5; i++) {
            int consecutiveEmpty = 0;
            for (int j = 0; j < 20; j++) {
                try {
                    // 这里可以扩展，按照不同CoinType来处理。目前只处理以太坊
                    DeterministicKey deterministicKey = HdWalletUtil.deterministicKey(masterPrivateKey, CoinType.ETHEREUM.getCoinType(), i, j);
                    String address = HdWalletUtil.getAddressFromKey(deterministicKey, CoinType.ETHEREUM.getCoinType());
                    String accountPath = deterministicKey.getPathAsString();
                    BigInteger transactionCount = ethService.getTransactionCount(address);
                    BigDecimal balance = ethService.getBalance(address);
                    // --- 探测余额 (真实场景需调用区块链RPC接口) ---
                    // 这里模拟：只有第 0 和 第 2 个账户有钱
                    boolean hasBalance = transactionCount.compareTo(BigInteger.ZERO) > 0 || balance.compareTo(BigDecimal.ZERO) > 0;
                    if (hasBalance) {
                        consecutiveEmpty = 0;
                        // 创建账户对象
                        AccountVO account = new AccountVO();
                        account.setAccountName("账户" + (i + 1) + " 编号：" + j);
                        log.debug("accountPath = {}", accountPath);
                        account.setAddress(address);

                        // 创建资产列表
                        List<AssetVO> assets = new ArrayList<>();

                        // 模拟资产 1: ETH
                        AssetVO ethAsset = new AssetVO();
                        ethAsset.setChain("ETH");
                        ethAsset.setSymbol("Ethereum");
                        ethAsset.setBalance(balance.toString());
                        ethAsset.setUsd("$未知");
                        ethAsset.setIcon("fa-brands fa-ethereum");
                        assets.add(ethAsset);


                        // 设置总资产估值 (简单拼接，实际应计算总和)
                        account.setTotalUsd("$ 未知");
                        account.setAssets(assets);

                        // 加入列表
                        accountList.add(account);
                    } else if (++consecutiveEmpty > MAX_EMPTY_GAP) {
                        break;
                    }

                } catch (Exception e) {
                    log.error("单个地址派生失败 i = {}", i, e);
                }
            }
        }

        return accountList;
    }
}