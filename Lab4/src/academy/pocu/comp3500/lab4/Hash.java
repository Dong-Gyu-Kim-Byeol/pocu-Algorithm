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
        final String hashBase64 = String.valueOf(crc32.getValue());
        return hashBase64;
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
            MessageDigest Sha1 = MessageDigest.getInstance("SHA-1");
            Sha1.update(plain.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(Sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    public static String getSha256(final String plain) {
        String hashBase64;
        try {
            MessageDigest Sha256 = MessageDigest.getInstance("SHA-256");
            Sha256.update(plain.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(Sha256.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    private Hash() {
    }
}
