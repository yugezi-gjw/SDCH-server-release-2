package com.varian.oiscn.base.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Photo Utility.<br>
 */
public final class PhotoUtil {

    /**
     * Encode the binary data to base64 String.<br>
     *
     * @param photoBytes binary data
     * @return Base64 String
     */
    public static String encode(byte[] photoBytes) {
        String base64Str = StringUtils.EMPTY;
        if (photoBytes != null) {
            base64Str = Base64.encodeBase64String(photoBytes);
            int mod4 = base64Str.length() % 4;
            if (mod4 > 0) {
                base64Str += "====".substring(mod4);
            }
        }
        return base64Str;
    }

    /**
     * Decode Photo String to binary.<br>
     *
     * @param photoStr Base64 String
     * @return binary data
     */
    public static byte[] decode(String photoStr) {
        byte[] base64Bytes = null;
        if (photoStr != null && Base64.isBase64(photoStr)) {
            base64Bytes = Base64.decodeBase64(photoStr);
        }
        return base64Bytes;
    }

    public static String generateUID(String deviceId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileSuffix = sdf.format(new Date());
        return deviceId.replace(" ", "_") + "-" + fileSuffix;
    }
}
