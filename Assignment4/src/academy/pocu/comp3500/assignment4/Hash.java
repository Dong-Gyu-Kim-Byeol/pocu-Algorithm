package academy.pocu.comp3500.assignment4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class Hash {
    private Hash() {
    }

    // ---

    public static long crc32(final byte[] data) {
        CRC32 crc32Creator = new CRC32();
        crc32Creator.update(data);
        final long crc32 = crc32Creator.getValue();
        return crc32;
    }

    public static long fnv1(final byte[] data) {
        final long PRIME_32 = 16777619;
        final long OFFSET_32 = 2166136261L;

        long hash = OFFSET_32;
        for (final byte d : data) {
            hash *= PRIME_32;
            hash ^= d;
        }

        return hash;
    }

    public static long fnv1a(final byte[] data) {
        final long PRIME_32 = 16777619;
        final long OFFSET_32 = 2166136261L;

        long hash = OFFSET_32;
        for (final byte d : data) {
            hash ^= d;
            hash *= PRIME_32;
        }

        return hash;
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
}
