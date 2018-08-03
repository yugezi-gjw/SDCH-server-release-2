package com.varian.oiscn.security.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by gbt1220 on 7/24/2017.
 */
@Slf4j
public class SHACoder {
    private static final String HMAC_SHA_256 = "HmacSHA256";

    private static MessageDigest MD_SHA_256 = null;

    static {
        try {
            MD_SHA_256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // Should investigate error before using System.
            log.error("SHACoder with NoSuchAlgorithmException: {}", e.getMessage());
        }
    }

    public static String macSHA256(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256 = Mac.getInstance(HMAC_SHA_256);
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), HMAC_SHA_256);
        sha256.init(secret_key);
        return new HexBinaryAdapter().marshal(sha256.doFinal(message.getBytes()));
    }

    /**
     * Encode the byte array to string with SHA-256 algorithm.<br>
     *
     * @param data The data to be encoded
     * @return Encoded String
     */
    public static String encodeSHA256(byte[] data) {
        byte[] digest = MD_SHA_256.digest(data);
        return new HexBinaryAdapter().marshal(digest);
    }
}
