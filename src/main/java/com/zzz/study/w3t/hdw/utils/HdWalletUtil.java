package com.zzz.study.w3t.hdw.utils;

import com.zzz.study.w3t.hdw.enums.CoinType;
import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

/**
 * @author zengsl
 */
public class HdWalletUtil {

    /**
     * 1. 生成助记词
     */
    public static String generateMnemonic() {
        MnemonicCode mc = MnemonicCode.INSTANCE;
        // 128位 -> 12个单词
        byte[] entropy = new byte[16];
        new SecureRandom().nextBytes(entropy);
        List<String> words = mc.toMnemonic(entropy);
        return String.join(" ", words);
    }

    /**
     * 2. 从助记词推导根密钥
     */

    public static DeterministicKey getMasterPrivateKey(String mnemonic) {
        if (mnemonic == null || mnemonic.isEmpty()) {
            throw new IllegalArgumentException("助记词不能为空");
        }
        List<String> words = Arrays.asList(mnemonic.split(" "));
        if (words.size() != 12) {
            throw new IllegalArgumentException("助记词长度不合法");
        }
        // 创建种子，password 为空字符串，creationTimeSeconds 为 0
        DeterministicSeed seed = DeterministicSeed.ofMnemonic(words, "");
        /*DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
        System.out.println(chain.getAccountPath());*/
        if (seed.getSeedBytes() == null) {
            return null;
        }
        return HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
    }

    /**
     * 3. 推导特定路径地址
     */
    public static String deriveAddress(String mnemonic, int coinType, int accountIndex, int addressIndex) {
        DeterministicKey deterministicKey = deterministicKey(mnemonic, coinType, accountIndex, addressIndex);
        if (deterministicKey == null) {
            return null;
        }
        return getAddressFromKey(deterministicKey, coinType);
    }

    public static DeterministicKey deterministicKey(String mnemonic, int coinType, int accountIndex, int addressIndex) {
        DeterministicKey masterPrivateKey = getMasterPrivateKey(mnemonic);
        if (masterPrivateKey == null) {
            return null;
        }
        // m代表master
        // m / purpose' / coin_type' / account' / change / address_index
        List<ChildNumber> path = Arrays.asList(
                new ChildNumber(44, true),
                new ChildNumber(coinType, true),
                new ChildNumber(accountIndex, true),
                ChildNumber.ZERO,
                new ChildNumber(addressIndex, false)
        );
        DeterministicHierarchy dh = new DeterministicHierarchy(masterPrivateKey);
        return dh.get(path, true, true);
    }

    /**
     * 4. 根据币种生成地址
     */
    private static String getAddressFromKey(DeterministicKey key, int coinType) {
        // 以太坊
        if (coinType == CoinType.ETHEREUM.getCoinType()) {
            // 1. 获取公钥 (不带 04 前缀的原始字节)
            byte[] pubKey = key.getPubKey();

            // 2. 使用 Keccak-256 哈希
            // 注意：不能用 bitcoinj 的 sha256hash160，那是比特币用的
            Keccak.DigestKeccak keccak = new Keccak.Digest256();
            byte[] hash = keccak.digest(pubKey);

            // 3. 取最后 20 字节
            byte[] addressBytes = Arrays.copyOfRange(hash, hash.length - 20, hash.length);

            // 4. 转为 16 进制并加 0x 前缀
            return "0x" + HexFormat.of().formatHex(addressBytes);

        } else {
            // 比特币或其他 (使用 bitcoinj 默认逻辑)
            return key.toAddress(ScriptType.P2PKH, BitcoinNetwork.TESTNET).toString();
        }
    }

    public static void main(String[] args) {
        try {
            // 助记词: recipe volume blue nasty inquiry sea baby farm business world fitness pool
            // 以太坊地址 (m/44H/60H/0H/0/0): 0x101fad54ae25076579f2f7ec92b4209b33b5f201 TODO 地址格式和METAMASK一致，为什么将助记词导入之后无法关联到呢？
            // https://sepolia.etherscan.io/tx/0x8984386c4d8304b229f54d76b529e508aea55c049813970e7005a2eae5ad2c30
            // 以太坊地址：0x101fad54ae25076579f2f7ec92b4209b33b5f201
            // 67391719122516144521706562135748408655814537406568014084488913715369885812132
            // 转0.01个以太测试

            // 1. 生成助记词
            String mnemonic = generateMnemonic();
            System.out.println("助记词: " + mnemonic);

            int accountIndex = 0, addressIndex = 0;
            String btcAddress = deriveAddress(mnemonic, CoinType.BITCOIN.getCoinType(), accountIndex, addressIndex);
            System.out.println("比特币地址 (m/44'/0'/0'/0/0): " + btcAddress);

            String ethAddress = deriveAddress(mnemonic, CoinType.ETHEREUM.getCoinType(), accountIndex, addressIndex);
            System.out.println("以太坊地址 (m/44'/0'/0'/0/0): " + ethAddress);


            int accountIndex2 = 1, addressIndex2 = 0;
            String btcAddress2 = deriveAddress(mnemonic, CoinType.BITCOIN.getCoinType(), accountIndex2, addressIndex2);
            System.out.println("比特币地址 (m/44'/0'/0'/0/0): " + btcAddress2);

            String ethAddress2 = deriveAddress(mnemonic, CoinType.ETHEREUM.getCoinType(), accountIndex2, addressIndex2);
            System.out.println("以太坊地址 (m/44'/0'/0'/0/0): " + ethAddress2);


            DeterministicKey deterministicKey = deterministicKey("recipe volume blue nasty inquiry sea baby farm business world fitness pool", CoinType.ETHEREUM.getCoinType(), 0, 0);
            System.out.println("Path: " + (deterministicKey != null ? deterministicKey.getPath().toString() : " 空"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}