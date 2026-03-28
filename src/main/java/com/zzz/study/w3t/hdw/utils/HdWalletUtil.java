package com.zzz.study.w3t.hdw.utils;

import com.zzz.study.w3t.hdw.enums.CoinType;
import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.web3j.crypto.Credentials;

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

    public static DeterministicKey deterministicKey(DeterministicKey masterKey, int coinType, int accountIndex, int addressIndex) {
        // m代表master
        // m / purpose' / coin_type' / account' / change / address_index
        List<ChildNumber> path = Arrays.asList(
                new ChildNumber(44, true),
                new ChildNumber(coinType, true),
                new ChildNumber(accountIndex, true),
                ChildNumber.ZERO,
                new ChildNumber(addressIndex, false)
        );
        DeterministicHierarchy dh = new DeterministicHierarchy(masterKey);
        return dh.get(path, true, true);
    }

    public static String deriveAddress(DeterministicKey masterKey, int coinType, int accountIndex, int addressIndex) {
        // m代表master
        // m / purpose' / coin_type' / account' / change / address_index
        List<ChildNumber> path = Arrays.asList(
                new ChildNumber(44, true),
                new ChildNumber(coinType, true),
                new ChildNumber(accountIndex, true),
                ChildNumber.ZERO,
                new ChildNumber(addressIndex, false)
        );
        DeterministicHierarchy dh = new DeterministicHierarchy(masterKey);
        return getAddressFromKey(dh.get(path, true, true), coinType);
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
    public static String getAddressFromKey(DeterministicKey key, int coinType) {
        // 以太坊
        if (coinType == CoinType.ETHEREUM.getCoinType()) {
            Credentials credentials = Credentials.create(key.getPrivateKeyAsHex());
            return credentials.getAddress();
        } else {
            // 比特币或其他 (使用 bitcoinj 默认逻辑)
            return key.toAddress(ScriptType.P2PKH, BitcoinNetwork.TESTNET).toString();
        }
    }

    public static void main(String[] args) {
        try {

            // 1. 生成助记词
            /*String mnemonic = generateMnemonic();
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
*/

            DeterministicKey deterministicKey = deterministicKey("recipe volume blue nasty inquiry sea baby farm business world fitness pool", CoinType.ETHEREUM.getCoinType(), 0, 0);
            System.out.println("Path: " + (deterministicKey != null ? deterministicKey.getPath().toString() : " 空"));
            System.out.println("private Key: 0x"+deterministicKey.getPrivateKeyAsHex());
            String addressFromKey = getAddressFromKey(deterministicKey, CoinType.ETHEREUM.getCoinType());
            System.out.println("address: " + addressFromKey);

            DeterministicKey deterministicKey2 = deterministicKey("recipe volume blue nasty inquiry sea baby farm business world fitness pool", CoinType.ETHEREUM.getCoinType(), 1, 0);
            System.out.println("Path: " + (deterministicKey2 != null ? deterministicKey2.getPath().toString() : " 空"));
            System.out.println("private Key: 0x"+deterministicKey2.getPrivateKeyAsHex());
            String addressFromKey2 = getAddressFromKey(deterministicKey2, CoinType.ETHEREUM.getCoinType());
            System.out.println("address: " + addressFromKey2);


            Credentials credentials = Credentials.create(deterministicKey.getPrivateKeyAsHex());
            System.out.println("w3j address: " + credentials.getAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
