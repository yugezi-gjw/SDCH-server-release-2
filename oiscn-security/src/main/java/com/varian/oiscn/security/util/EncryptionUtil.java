/**
 *
 */
package com.varian.oiscn.security.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encryption / Decryption Utility Class.<br>
 */
@Slf4j
public class EncryptionUtil {

    private static final String KEY_IV = "R@cD143BEAtVeM!o";
    private static final String KEY_SPEC = "B@r523MK!Gr394D#";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private static final String UTF8 = "UTF-8";
    private static final String AES = "AES";
    private static final String MD5 = "md5";
    protected static MessageDigest ALGORITHM_MD5 = null;
    protected static IvParameterSpec IV_SPEC = null;
    protected static SecretKeySpec S_KEY_SPEC = null;
    protected static Cipher CIPHER_ENCRYPTION = null;
    protected static Cipher CIPHER_DECRYPTION = null;

    public static void init() throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        ALGORITHM_MD5 = MessageDigest.getInstance(MD5);

        IV_SPEC = new IvParameterSpec(KEY_IV.getBytes(UTF8));
        S_KEY_SPEC = new SecretKeySpec(KEY_SPEC.getBytes(UTF8), AES);

        CIPHER_ENCRYPTION = Cipher.getInstance(CIPHER_TRANSFORMATION);
        CIPHER_ENCRYPTION.init(Cipher.ENCRYPT_MODE, S_KEY_SPEC, IV_SPEC);

        CIPHER_DECRYPTION = Cipher.getInstance(CIPHER_TRANSFORMATION);
        CIPHER_DECRYPTION.init(Cipher.DECRYPT_MODE, S_KEY_SPEC, IV_SPEC);
    }

    /**
     * Return the hash value of a text.<br>
     *
     * @param text The text to be hashed
     * @return hash value
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String hashMD5(String text) {
        if (ALGORITHM_MD5 == null) {
            try {
                init();
            } catch (Exception e) {
                log.error("hashMD5 Initialization is down with exception:{}", e.getMessage());
                return StringUtils.EMPTY;
            }
        }
        if (StringUtils.isBlank(text)) {
            return StringUtils.EMPTY;
        }
        byte[] hash = ALGORITHM_MD5.digest(text.getBytes());
        return new BigInteger(1, hash).toString(16).toUpperCase();
    }

    /**
     * Encrypt clear text by the given CIPHER.<br>
     *
     * @param clearText Clear Text
     * @return Encrypted Text
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public static String encrypt(String clearText) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        if (StringUtils.isBlank(clearText)) {
            return StringUtils.EMPTY;
        }
        if (CIPHER_ENCRYPTION == null) {
            init();
        }
        byte[] encryptedByte = CIPHER_ENCRYPTION.doFinal(clearText.getBytes());
        return Base64.encodeBase64String(encryptedByte);
    }

    /**
     * Return Clear Text from Encrypted Text.<br>
     *
     * @param encrypted Encrypted Text
     * @return Clear Text
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public static String decrypt(String encrypted) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        if (StringUtils.isBlank(encrypted)) {
            return StringUtils.EMPTY;
        }

        if (CIPHER_DECRYPTION == null) {
            init();
        }
        byte[] original = CIPHER_DECRYPTION.doFinal(Base64.decodeBase64(encrypted.getBytes(UTF8)));
        return new String(original);
    }
}
