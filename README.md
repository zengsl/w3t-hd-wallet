# HD(Hierarchical Deterministic) Wallet Demo

分层确定性钱包

> https://liaoxuefeng.com/books/blockchain/bitcoin/hd-wallet/index.html

助记词->种子->根私钥->子私钥->还可以继续派生私钥->公钥->地址

对于任意一个私钥k，总是可以根据索引计算它的下一层私钥kn：

`k n=hdkey(k,n)`

1. 生成助记词
2. 生成seed
3. 生成根私钥
4. 路径派生出公私钥
5. 公钥根据不同链的规则生成对应的地址编码