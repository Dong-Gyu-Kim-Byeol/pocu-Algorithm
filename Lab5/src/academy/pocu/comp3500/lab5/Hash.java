package academy.pocu.comp3500.lab5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public static byte[] sha256(final byte[] plain) {
        byte[] hash;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(plain);
            hash = sha256.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hash;
    }

    private Hash() {
    }
}
