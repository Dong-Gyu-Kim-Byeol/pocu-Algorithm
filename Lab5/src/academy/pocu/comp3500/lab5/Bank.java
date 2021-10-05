package academy.pocu.comp3500.lab5;

import java.util.HashMap;

public class Bank {
    private final HashMap<String, Long> accounts;

    // public
    public Bank(byte[][] pubKeys, final long[] amounts) {
        this.accounts = new HashMap<String, Long>();

        int index = 0;
        for (final byte[] pubKey : pubKeys) {
            if (amounts[index] < 0) {
                continue;
            }
            this.accounts.put(encodeToHexString(pubKey), amounts[index++]);
        }
    }

    public long getBalance(final byte[] pubKey) {
        final String pubKeyString = encodeToHexString(pubKey);
        final Long balance = this.accounts.get(pubKeyString);
        if (balance == null) {
            return 0;
        } else {
            return balance;
        }
    }

    public boolean transfer(final byte[] from, byte[] to, final long amount, final byte[] signature) {
        if (amount < 1) {
            return false;
        }

        final String fromString = encodeToHexString(from);
        final String toString = encodeToHexString(to);

        byte[] fromToAmount = new byte[from.length + to.length + Long.BYTES];
        {
            int byteIndex = 0;
            for (final byte oneByte : from) {
                fromToAmount[byteIndex++] = (oneByte);
            }
            for (final byte oneByte : to) {
                fromToAmount[byteIndex++] = (oneByte);
            }
            for (int i = 64 - 8; i >= 0; i -= 8) {
                fromToAmount[byteIndex++] = (byte) (amount >> i);
            }
        }
        final byte[] fromToAmountHash = Hash.sha256(fromToAmount);
        final String fromToAmountHashString = encodeToHexString(fromToAmountHash);

        final byte[] signatureHash = Rsa.decryptWithPublicKeyOrNull(signature, Rsa.convertPublicKey(from));
        if (signatureHash == null) {
            return false;
        }
        final String signatureHashString = encodeToHexString(signatureHash);

        if (fromToAmountHashString.equals(signatureHashString) == false) {
            return false;
        }

        final Long fromBalance = this.accounts.get(fromString);
        if (fromBalance == null) {
            return false;
        }
        if (fromBalance < amount) {
            return false;
        }

        final Long toBalance = this.accounts.get(toString);
        if (toBalance == null) {
            return false;
        }
        if (Long.MAX_VALUE - toBalance < amount) {
            return false;
        }

        this.accounts.put(fromString, fromBalance - amount);
        this.accounts.put(toString, toBalance + amount);

        return true;
    }

    // private
    private static byte[] decodeFromHexString(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            int firstDigit = Character.digit(hexString.charAt(i), 16);
            int secondDigit = Character.digit(hexString.charAt(i + 1), 16);
            bytes[i / 2] = (byte) ((firstDigit << 4) + secondDigit);
        }
        return bytes;
    }

    private static String encodeToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte oneByte : bytes) {
            result.append(String.format("%02x", oneByte));
        }
        return result.toString();
    }
}