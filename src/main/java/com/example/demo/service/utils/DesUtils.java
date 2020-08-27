package com.example.demo.service.utils;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
/**
 * 3des加密，解密
 * @author ywj
 */
public class DesUtils {
    public static final String key = "32ncw43d204a32m429n3267k";// 密钥

    // 3DESECB加密,key必须是长度大于等于 3*8 = 24 位
    public static String encryptThreeDESECB(String src) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");

        SecretKey secureKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secureKey);
        byte[] b = cipher.doFinal(src.getBytes());

        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b).replaceAll("\r", "").replaceAll("\n", "");

    }

    // 3DESECB解密,key必须是长度大于等于 3*8 = 24 位
    public static String decryptThreeDESECB(String src) throws Exception {
        // --通过base64,将字符串转成byte数组
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] byteSrc = decoder.decodeBuffer(src);
        // --解密的key
        DESedeKeySpec dks = new DESedeKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey securekey = keyFactory.generateSecret(dks);

        // --Chipher对象解密
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        byte[] retByte = cipher.doFinal(byteSrc);

        return new String(retByte);
    }
}
