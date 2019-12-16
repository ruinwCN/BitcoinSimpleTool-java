package com.sp.btc;

/**
 * Created by Blue on 2019/11/6.
 */
public enum AddressPrefixEnum {
    BitcoinAddress,
    PayToScriptHashAddress,
    BitcoinTestnetAddress,
    TestnetScriptHash,
    PrivateKey,
    TestnetPrivateKey,
    ;

    public static byte getPrefixHex(AddressPrefixEnum addressPrefixEnum) {
        switch (addressPrefixEnum) {
            case BitcoinAddress:
                return (byte) 0x00;//0x00  1
            case PayToScriptHashAddress:
                return (byte) 0x05;//0x05  3
            case BitcoinTestnetAddress:
                return (byte) 0x6f;//0x6F  m or n
            case PrivateKey:
                return (byte) 0x80;//0x80 5
            case TestnetScriptHash:
                return (byte) 0xc4;//0xc4 2
            case TestnetPrivateKey:
                return (byte) 0xEF;//0xEF 9 | c
            default:
                return (byte) 0x00;
        }
    }
}
