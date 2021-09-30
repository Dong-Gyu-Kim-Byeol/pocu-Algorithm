package academy.pocu.comp3500.lab4;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.CRC32;

public class Hash {
    public static String getCrc32(final String plain) {
        CRC32 crc32 = new CRC32();
        crc32.update(plain.getBytes(StandardCharsets.UTF_8));
        final String hashString = String.valueOf(crc32.getValue());
        return hashString;
    }

    public static String getMd2(final String plain) {
        String hashBase64;
        try {
            MessageDigest md2 = MessageDigest.getInstance("MD2");
            md2.update(plain.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(md2.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    public static String getMd5(final String plain) {
        String hashBase64;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(plain.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    public static String getSha1(final String plain) {
        String hashBase64;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(plain.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    public static String getSha256(final String plain) {
        String hashBase64;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(plain.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(sha256.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    private Hash() {
    }
}
