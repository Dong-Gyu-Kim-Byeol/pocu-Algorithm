package academy.pocu.comp3500.lab5;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Rsa {
    public static KeyPair getKeyPair() {
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, new SecureRandom());

            final KeyPair pair = generator.generateKeyPair();
            return pair;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static byte[] encrypt(final byte[] plaintext, final PublicKey publicKey) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            final byte[] ciphertext = cipher.doFinal(plaintext);
            return ciphertext;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(final byte[] ciphertext, final PrivateKey privateKey) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            final byte[] plaintext = cipher.doFinal(ciphertext);
            return plaintext;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptWithPrivateKey(final byte[] plaintext, final PrivateKey privateKey) {
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            final byte[] ciphertext = cipher.doFinal(plaintext);
            return ciphertext;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptWithPublicKeyOrNull(final byte[] encryptedMessage, final PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            final byte[] plaintext = cipher.doFinal(encryptedMessage);
            return plaintext;
        } catch (BadPaddingException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static KeyPair convertKeyPair(final byte[] encodedPublicKey, final byte[] encodedPrivateKey) {
        return new KeyPair(Rsa.convertPublicKey(encodedPublicKey), Rsa.convertPrivateKey(encodedPrivateKey));
    }

    public static PrivateKey convertPrivateKey(final byte[] encodedPrivateKey) {
        try {
            final EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            final KeyFactory kf = KeyFactory.getInstance("RSA");

            final PrivateKey privateKey = kf.generatePrivate(privateKeySpec);
            return privateKey;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert PrivateKey from provided encoded key", e);
        }
    }

    public static PublicKey convertPublicKey(final byte[] encodedPublicKey) {
        try {
            final EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            final KeyFactory kf = KeyFactory.getInstance("RSA");

            final PublicKey publicKey = kf.generatePublic(publicKeySpec);
            return publicKey;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert PublicKey from provided encoded key", e);
        }
    }

    // private
    private Rsa() {
    }
}
