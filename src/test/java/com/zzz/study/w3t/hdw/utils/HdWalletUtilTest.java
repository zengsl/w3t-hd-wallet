package com.zzz.study.w3t.hdw.utils;

import com.zzz.study.w3t.hdw.enums.CoinType;
import org.bitcoinj.crypto.DeterministicKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HdWalletUtil 单元测试
 */
class HdWalletUtilTest {

    @Test
    @DisplayName("测试生成助记词")
    void testGenerateMnemonic() {
        String mnemonic = HdWalletUtil.generateMnemonic();

        assertNotNull(mnemonic, "助记词不应为空");
        assertFalse(mnemonic.isEmpty(), "助记词不应为空字符串");

        List<String> words = Arrays.asList(mnemonic.split(" "));
        assertEquals(12, words.size(), "助记词应该包含 12 个单词");

        for (String word : words) {
            assertFalse(word.isEmpty(), "每个单词都不应为空");
            assertTrue(word.matches("[a-z]+"), "每个单词都应该是小写字母");
        }
    }

    @Test
    @DisplayName("测试多次生成助记词的唯一性")
    void testGenerateMultipleMnemonics() {
        String mnemonic1 = HdWalletUtil.generateMnemonic();
        String mnemonic2 = HdWalletUtil.generateMnemonic();
        String mnemonic3 = HdWalletUtil.generateMnemonic();

        assertNotEquals(mnemonic1, mnemonic2, "每次生成的助记词应该不同");
        assertNotEquals(mnemonic2, mnemonic3, "每次生成的助记词应该不同");
        assertNotEquals(mnemonic1, mnemonic3, "每次生成的助记词应该不同");
    }

    @Test
    @DisplayName("测试从助记词推导主私钥")
    void testGetMasterPrivateKey() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";

        DeterministicKey masterKey = HdWalletUtil.getMasterPrivateKey(mnemonic);

        assertNotNull(masterKey, "主私钥不应为空");
        assertNotNull(masterKey.getPrivKeyBytes(), "私钥字节不应为空");
        assertTrue(masterKey.getPrivKeyBytes().length > 0, "私钥字节长度应大于 0");
    }

    @Test
    @DisplayName("测试无效助记词返回 null")
    void testInvalidMnemonic() {
        String invalidMnemonic = "invalid words that don't exist";
        assertThrows(Exception.class, () -> {
            HdWalletUtil.getMasterPrivateKey(invalidMnemonic);
        }, "无效助记词应该抛出异常");
    }

    @Test
    @DisplayName("测试推导比特币地址")
    void testDeriveBitcoinAddress() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        int coinType = CoinType.BITCOIN.getCoinType();
        int accountIndex = 0;
        int addressIndex = 0;

        String address = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, addressIndex);

        assertNotNull(address, "比特币地址不应为空");
        assertFalse(address.isEmpty(), "比特币地址不应为空字符串");
        assertTrue(address.startsWith("m") || address.startsWith("n") || address.startsWith("t"),
                "比特币测试网地址应以 m/n/t 开头");
    }

    @Test
    @DisplayName("测试推导以太坊地址")
    void testDeriveEthereumAddress() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        int coinType = CoinType.ETHEREUM.getCoinType();
        int accountIndex = 0;
        int addressIndex = 0;

        String address = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, addressIndex);

        assertNotNull(address, "以太坊地址不应为空");
        assertFalse(address.isEmpty(), "以太坊地址不应为空字符串");
        assertTrue(address.startsWith("0x"), "以太坊地址应以 0x 开头");
        assertEquals(42, address.length(), "以太坊地址长度应为 42 个字符 (包含 0x)");

        String hexPart = address.substring(2);
        assertTrue(hexPart.matches("[0-9a-fA-F]+"), "以太坊地址 hex 部分应只包含十六进制字符");
    }

    @Test
    @DisplayName("测试推导不同账户索引的地址")
    void testDeriveDifferentAccountAddresses() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        int coinType = CoinType.ETHEREUM.getCoinType();

        String address0 = HdWalletUtil.deriveAddress(mnemonic, coinType, 0, 0);
        String address1 = HdWalletUtil.deriveAddress(mnemonic, coinType, 1, 0);
        String address2 = HdWalletUtil.deriveAddress(mnemonic, coinType, 2, 0);

        assertNotNull(address0);
        assertNotNull(address1);
        assertNotNull(address2);

        assertNotEquals(address0, address1, "不同账户索引应生成不同地址");
        assertNotEquals(address1, address2, "不同账户索引应生成不同地址");
        assertNotEquals(address0, address2, "不同账户索引应生成不同地址");
    }

    @Test
    @DisplayName("测试推导不同地址索引的地址")
    void testDeriveDifferentAddressIndexes() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        int coinType = CoinType.ETHEREUM.getCoinType();
        int accountIndex = 0;

        String address0 = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, 0);
        String address1 = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, 1);
        String address2 = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, 2);

        assertNotNull(address0);
        assertNotNull(address1);
        assertNotNull(address2);

        assertNotEquals(address0, address1, "不同地址索引应生成不同地址");
        assertNotEquals(address1, address2, "不同地址索引应生成不同地址");
        assertNotEquals(address0, address2, "不同地址索引应生成不同地址");
    }

    @Test
    @DisplayName("测试推导确定性密钥")
    void testDeterministicKey() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        int coinType = CoinType.ETHEREUM.getCoinType();
        int accountIndex = 0;
        int addressIndex = 0;

        DeterministicKey key = HdWalletUtil.deterministicKey(mnemonic, coinType, accountIndex, addressIndex);

        assertNotNull(key, "确定性密钥不应为空");
        assertNotNull(key.getPath(), "密钥路径不应为空");

        List<org.bitcoinj.crypto.ChildNumber> path = key.getPath();
        assertEquals(5, path.size(), "BIP44 路径应包含 5 个子路径");
    }

    @Test
    @DisplayName("测试相同参数生成相同地址")
    void testDeterministicAddressGeneration() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        int coinType = CoinType.ETHEREUM.getCoinType();
        int accountIndex = 0;
        int addressIndex = 0;

        String address1 = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, addressIndex);
        String address2 = HdWalletUtil.deriveAddress(mnemonic, coinType, accountIndex, addressIndex);

        assertEquals(address1, address2, "相同参数应生成相同地址");
    }

    @Test
    @DisplayName("测试助记词包含多个连续空格时的处理")
    void testMnemonicWithMultipleSpaces() {
        String mnemonicWithSingleSpaces = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        String mnemonicWithMultipleSpaces = "recipe  volume   blue nasty inquiry sea baby farm business world fitness pool";

        DeterministicKey key1 = HdWalletUtil.getMasterPrivateKey(mnemonicWithSingleSpaces);
        assertNotNull(key1, "单个空格的助记词应能正常解析");

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> HdWalletUtil.getMasterPrivateKey(mnemonicWithMultipleSpaces),
                "多个连续空格的助记词应该抛出异常");
        assertTrue(exception.getMessage().contains("长度"), "异常信息应提示长度不合法");
    }

    @Test
    @DisplayName("测试助记词前后有空格时的处理")
    void testMnemonicWithLeadingTrailingSpaces() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";
        String mnemonicWithSpaces = "  " + mnemonic + "  ";

        DeterministicKey key1 = HdWalletUtil.getMasterPrivateKey(mnemonic);
        DeterministicKey key2 = HdWalletUtil.getMasterPrivateKey(mnemonicWithSpaces.trim());

        assertNotNull(key1);
        assertNotNull(key2);
        assertArrayEquals(key1.getPrivKeyBytes(), key2.getPrivKeyBytes(),
                "trim 后应该能正常解析");
    }

    @Test
    @DisplayName("测试空助记词和 null 值处理")
    void testEmptyAndNullMnemonic() {
        assertThrows(IllegalArgumentException.class,
                () -> HdWalletUtil.getMasterPrivateKey(null),
                "null 值应该抛出异常");

        assertThrows(IllegalArgumentException.class,
                () -> HdWalletUtil.getMasterPrivateKey(""),
                "空字符串应该抛出异常");

        assertThrows(IllegalArgumentException.class,
                () -> HdWalletUtil.getMasterPrivateKey("   "),
                "纯空格应该抛出异常");
    }

    @Test
    @DisplayName("测试完整的 BIP44 路径推导")
    void testFullBIP44Path() {
        String mnemonic = "recipe volume blue nasty inquiry sea baby farm business world fitness pool";

        DeterministicKey ethKey = HdWalletUtil.deterministicKey(
                mnemonic,
                CoinType.ETHEREUM.getCoinType(),
                0,
                0
        );

        assertNotNull(ethKey);
        List<org.bitcoinj.crypto.ChildNumber> path = ethKey.getPath();

        assertEquals(5, path.size());
        assertEquals(44, path.get(0).num());
        assertTrue(path.get(0).isHardened());

        assertEquals(60, path.get(1).num());
        assertTrue(path.get(1).isHardened());

        assertEquals(0, path.get(2).num());
        assertTrue(path.get(2).isHardened());
    }
}
