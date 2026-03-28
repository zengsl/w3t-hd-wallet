package com.zzz.study.w3t.hdw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class EthService {

    // 这里配置你的以太坊节点 RPC URL
    // 主网可以用 Infura/Alchemy，或者本地节点
    private final Web3j web3j;

    /**
     * 1. 查询地址余额
     *
     * @param address 钱包地址
     * @return 余额 (单位: Ether)
     */
    public BigDecimal getBalance(String address) throws IOException {
        // 获取最新区块的余额
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();

        // 获取结果 (单位是 Wei)
        BigInteger balanceInWei = ethGetBalance.getBalance();

        // 将 Wei 转换为 Ether (1 ETH = 10^18 Wei)
        return Convert.fromWei(new BigDecimal(balanceInWei), Convert.Unit.ETHER);
    }

    /**
     * 2. 查询交易数量 (即 Nonce)
     *
     * @param address 钱包地址
     * @return 交易次数
     */
    public BigInteger getTransactionCount(String address) throws IOException {
        // 获取 PENDING 状态的交易计数，这样能包含尚未打包的交易，防止 Nonce 冲突
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                address,
                DefaultBlockParameterName.PENDING
        ).send();

        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * 判断地址是否为空（没有任何交易记录，包括转入和转出）
     *
     * @param address 钱包地址
     * @return true: 是空地址, false: 有交易记录
     */
    public boolean isEmptyAddress(String address) {
        boolean res;
        try {
            res = this.getBalance(address).compareTo(BigDecimal.ZERO) > 0 && this.getTransactionCount(address).compareTo(BigInteger.ZERO) > 0;
        } catch (IOException e) {
            log.error("isEmptyAddress判断错误", e);
            res = false;
        }
        return res;
    }
}
