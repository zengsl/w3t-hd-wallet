package com.zzz.study.w3t.hdw;

import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicKeyChain;
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
    public static DeterministicKey getMasterKey(String mnemonic) {
        /*try {
            List<String> words = Arrays.asList(mnemonic.split(" "));
            // 创建种子，password 为空字符串，creationTimeSeconds 为 0
            DeterministicSeed seed = DeterministicSeed.ofMnemonic(words, "");

            // 构建密钥链
            DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
            // 获取主密钥 (m)
            return chain.getMasterKey();
        } catch (MnemonicException.MnemonicWordException | MnemonicException.MnemonicChecksumException | MnemonicException.MnemonicLengthException e) {
            throw new RuntimeException("助记词无效", e);
        }*/
        return null;
    }

    /**
     * 3. 推导特定路径地址
     */
    public static String deriveAddress(String mnemonic, int coinType, int accountIndex, int addressIndex) {
        /*DeterministicKey masterKey = getMasterKey(mnemonic);

        // 路径: m/44'
        // 注意：这里必须从 masterKey 的私钥字节开始构建父密钥
        DeterministicKey purposeKey = HDKeyDerivation.createMasterPrivateKey(masterKey.getPrivKeyBytes())
                .deriveChild(44, true);

        // 路径: m/44'/coin_type'
        DeterministicKey coinTypeKey = purposeKey.deriveChild(coinType, true);

        // 路径: m/44'/coin_type'/account'
        DeterministicKey accountKey = coinTypeKey.deriveChild(accountIndex, true);

        // 路径: m/44'/coin_type'/account'/0 (External Chain)
        DeterministicKey changeKey = accountKey.deriveChild(0, false);

        // 路径: m/44'/coin_type'/account'/0/address_index
        DeterministicKey addressKey = changeKey.deriveChild(addressIndex, false);

        return getAddressFromKey(addressKey, coinType);*/
        List<String> words = Arrays.asList(mnemonic.split(" "));
        // 创建种子，password 为空字符串，creationTimeSeconds 为 0
        DeterministicSeed seed = DeterministicSeed.ofMnemonic(words, "");

//        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed).build();
//        System.out.println(chain.getAccountPath());

        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed.getSeedBytes());
        // m / purpose' / coin_type' / account' / change / address_index
        List<ChildNumber> path = Arrays.asList(
                new ChildNumber(44, true),
                new ChildNumber(coinType, true),
                new ChildNumber(accountIndex, true),
                ChildNumber.ZERO,
                new ChildNumber(addressIndex, false)
        );
        DeterministicHierarchy dh = new DeterministicHierarchy(masterPrivateKey);
        DeterministicKey deterministicKey = dh.get(path, true, true);
        System.out.println("Path: " + deterministicKey.getPath().toString());
        return getAddressFromKey(deterministicKey, coinType);
    }

    /**
     * 4. 根据币种生成地址
     */
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
            // 1. 生成助记词
            String mnemonic = generateMnemonic();
            System.out.println("助记词: " + mnemonic);

            int accountIndex = 0, addressIndex = 0;
            String btcAddress = deriveAddress(mnemonic, CoinType.BITCOIN.getCoinType(), accountIndex, addressIndex);
            System.out.println("比特币地址 (m/44'/0'/0'/0/0): " + btcAddress);

            String ethAddress = deriveAddress(mnemonic, CoinType.ETHEREUM.getCoinType(), accountIndex, addressIndex);
            System.out.println("以太坊地址 (m/44'/0'/0'/0/0): " + ethAddress);


            int accountIndex2 = 1, addressIndex2 = 1;
            String btcAddress2 = deriveAddress(mnemonic, CoinType.BITCOIN.getCoinType(), accountIndex2, addressIndex2);
            System.out.println("比特币地址 (m/44'/0'/0'/0/0): " + btcAddress2);

            String ethAddress2 = deriveAddress(mnemonic, CoinType.ETHEREUM.getCoinType(), accountIndex2, addressIndex2);
            System.out.println("以太坊地址 (m/44'/0'/0'/0/0): " + ethAddress2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}