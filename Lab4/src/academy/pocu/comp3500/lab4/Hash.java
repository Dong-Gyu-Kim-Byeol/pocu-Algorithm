package academy.pocu.comp3500.lab4;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.CRC32;

public class Hash {
    public static String getSha256(final String plain) {
        String hashBase64;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(plain.getBytes(StandardCharsets.UTF_8));
            sha256.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashBase64;
    }

    private Hash() {
    }
}
