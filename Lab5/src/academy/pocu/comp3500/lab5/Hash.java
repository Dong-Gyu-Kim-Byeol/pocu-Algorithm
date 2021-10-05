package academy.pocu.comp3500.lab5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class Hash {
    public static long getCrc32(final byte[] data) {
        CRC32 crc32Creator = new CRC32();
        crc32Creator.update(data);
        final long crc32 = crc32Creator.getValue();
        return crc32;
    }

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
