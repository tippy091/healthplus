package vn.com.nested.backend.common.uis.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author tippy091
 * @created 02/06/2025
 * @project server
 **/

public class CryptoUtil {
    private static final Logger LOGGER = LogManager.getLogger(CryptoUtil.class);
    private static final String ALGORITHM_SHA256 = "SHA-256";
    private static final String ALGORITHM_MD5 = "MD5";

    private CryptoUtil() {
    }

    public static String sha256(String plaintext) {
        StringBuilder hexString = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));

            for(int i = 0; i < hash.length; ++i) {
                String hex = String.format("%02x", hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException var6) {
            LOGGER.error("Exception.msg=" + var6.getMessage(), var6);
        }

        return hexString.toString();
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, messageDigest);

            String hashtext;
            for(hashtext = number.toString(16); hashtext.length() < 32; hashtext = "0" + hashtext) {
            }

            return hashtext;
        } catch (NoSuchAlgorithmException var5) {
            LOGGER.error("Exception.msg=" + var5.getMessage(), var5);
            return null;
        }
    }
}

