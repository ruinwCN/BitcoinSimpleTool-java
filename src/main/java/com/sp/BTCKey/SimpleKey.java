package com.sp.BTCKey;

import com.sp.btc.AddressPrefixEnum;
import com.sp.btc.NetworkEnum;
import com.sp.btc.crypto.Base58;
import org.apache.tomcat.util.buf.HexUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by Blue on 2019/11/7.
 */
public class SimpleKey implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(SimpleKey.class);
    private Boolean showTag = true;

    private String privateKey;
    private Boolean compression;

    public SimpleKey(String priv) {
        if (priv != null) {
            this.privateKey = priv;
            return;
        }
        this.privateKey = HexUtils.toHexString(createPrivateKey());
    }

    public SimpleKey() {
        this.privateKey = HexUtils.toHexString(createPrivateKey());
    }

    public Boolean getCompression() {
        return compression;
    }

    public void setCompression(Boolean compression) {
        this.compression = compression;
    }

    public Boolean getShowTag() {
        return showTag;
    }

    public void setShowTag(Boolean showTag) {
        this.showTag = showTag;
    }


    public boolean setPrivateKeyWif(String privateKeyWif) throws Exception {
        byte[] dcode = Base58.decode(privateKeyWif);
        byte[] bVersion = new byte[1];
        byte[] base = new byte[dcode.length - 1 - 4];
        byte[] checkSum = new byte[4];
        System.arraycopy(dcode, 0, bVersion, 0, 1);
        System.arraycopy(dcode, 1, base, 0, dcode.length - 1 - 4);
        System.arraycopy(dcode, dcode.length - 4, checkSum, 0, 4);

        byte[] base_r = new byte[32];
        System.arraycopy(base, 0, base_r, 0, 32);

        byte[] checksum_base = new byte[dcode.length - 4];
        System.arraycopy(dcode, 0, checksum_base, 0, dcode.length - 4);
        byte[] checksumCal = null;
        try {
            checksumCal = getDoubleSHA256CheckSum(checksum_base);
        } catch (NoSuchAlgorithmException e) {
            log.error("checksum error {}", e.getLocalizedMessage());
            return false;
        }

        if (!HexUtils.toHexString(checksumCal).equals(HexUtils.toHexString(checkSum))) {
            log.error("checksum error not same");
            throw new Exception("checksum error");
        }
        privateKey = HexUtils.toHexString(base_r);
        return true;
    }

    // get private key as hex
    public String getPrivateKeyAsHex() {
        return privateKey;
    }

    // get private key as wif
    public String getPrivateKeyAsWIF(NetworkEnum networkEnum, Boolean compression) {
        if (null == privateKey) {
            return null;
        }
        String version = privateKey;
        AddressPrefixEnum addressPrefixEnum;
        switch (networkEnum) {
            case BTCMainnet:
                addressPrefixEnum = AddressPrefixEnum.PrivateKey;
                break;
            case BTCTestNet:
                addressPrefixEnum = AddressPrefixEnum.TestnetPrivateKey;
                break;
            default:
                addressPrefixEnum = AddressPrefixEnum.PrivateKey;
        }
        byte bVersion = AddressPrefixEnum.getPrefixHex(addressPrefixEnum);
        if (compression) {
            version = version + "01";
        }
        byte[] base = HexUtils.fromHexString(version);
        ByteArrayOutputStream baseOS = new ByteArrayOutputStream();
        baseOS.write(bVersion);  // version prefix
        baseOS.write(base, 0, base.length);
        byte[] checksum = null;
        try {
            checksum = getDoubleSHA256CheckSum(baseOS.toByteArray());
        } catch (NoSuchAlgorithmException e) {
            log.error("checksum error", e.getLocalizedMessage());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(bVersion);  // version prefix
        baos.write(base, 0, base.length);
        assert checksum != null;
        baos.write(checksum, 0, checksum.length);
        return Base58.encode(baos.toByteArray());
    }

    // get public key as hex
    public String getPublicKeyAsHex() {
        return HexUtils.toHexString(getPublicKey(false));
    }

    // get public key as Compressed
    public String getPublicKeyAsCompressed() {
        return HexUtils.toHexString(getPublicKey(true));
    }

    // get p2pkh
    public String getP2PKHAddress(NetworkEnum networkEnum, boolean compression) throws NoSuchAlgorithmException {
        byte[] address = getP2PKHAddressAsHex(compression);

        AddressPrefixEnum addressPrefixEnum;
        switch (networkEnum) {
            case BTCMainnet:
                addressPrefixEnum = AddressPrefixEnum.BitcoinAddress;
                break;
            case BTCTestNet:
                addressPrefixEnum = AddressPrefixEnum.BitcoinTestnetAddress;
                break;
            default:
                addressPrefixEnum = AddressPrefixEnum.BitcoinAddress;
        }
        byte prefixHex = AddressPrefixEnum.getPrefixHex(addressPrefixEnum);
        ByteArrayOutputStream baseOS = new ByteArrayOutputStream();
        baseOS.write(prefixHex);  // version prefix
        baseOS.write(address, 0, address.length);
        byte[] code = baseOS.toByteArray();
        byte[] checksum = getDoubleSHA256CheckSum(code);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(prefixHex);  // version prefix
        baos.write(address, 0, address.length);
        baos.write(checksum, 0, checksum.length);
        return Base58.encode(baos.toByteArray());
    }

    // pay to pubkey hash (aka pay to address)
    public byte[] getP2PKHAddressAsHex(boolean compression) {
        byte[] publicKeyBytes = getPublicKey(compression);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] dataOneHash = digest.digest(publicKeyBytes);
            RIPEMD160Digest d = new RIPEMD160Digest();
            d.update(dataOneHash, 0, dataOneHash.length);
            byte[] dataDoubleHash = new byte[d.getDigestSize()];
            d.doFinal(dataDoubleHash, 0);
            return dataDoubleHash;
        } catch (NoSuchAlgorithmException e) {
            log.error("getAddress SHA-256 error", e.getLocalizedMessage());
        }
        return null;
    }

    // private get public key
    private byte[] getPublicKey(boolean compressed) {
        assert privateKey != null;
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPoint pointQ = spec.getG().multiply(new BigInteger(1, HexUtils.fromHexString(privateKey)));
        return pointQ.getEncoded(compressed);
    }

    // 生成随机私钥
    private byte[] createPrivateKey() {
        byte[] key = generateRandomKey();
        privateKey = HexUtils.toHexString(key);
        return key;
    }

    private byte[] generateRandomKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] seed = new byte[32];
        secureRandom.nextBytes(seed);
        return seed;
    }

    // double sha256CheckSum
    private byte[] getDoubleSHA256CheckSum(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] dataOneHash = digest.digest(data);
        byte[] dataDoubleHash = digest.digest(dataOneHash);
        return Arrays.copyOf(dataDoubleHash, 4);
    }
}
