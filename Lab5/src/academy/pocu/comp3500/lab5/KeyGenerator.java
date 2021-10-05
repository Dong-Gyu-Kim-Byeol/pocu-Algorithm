package academy.pocu.comp3500.lab5;

import java.math.BigInteger;

public class KeyGenerator {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.TWO;
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FIVE = BigInteger.valueOf(5);
    private static final BigInteger SIX = BigInteger.valueOf(6);

    public static boolean isPrime(final BigInteger number) {
        if (number.signum() <= 0) {
            return false;
        }

        final byte[] numberBytes = number.toByteArray();

        final short firstByte = (short) (numberBytes[0] & 0xff);
        if (numberBytes.length == 1 && firstByte == 1) {
            return false;
        }

        if (numberBytes.length == 1 && firstByte == 2) {
            return true;
        }
        if (numberBytes.length == 1 && firstByte == 3) {
            return true;
        }

        final short lastByte = (short) (numberBytes[numberBytes.length - 1] & 0xff);
        if (lastByte % 2 == 0) {
            return false;
        }

        if (lastByte % 3 == 0) {
            return false;
        }
        if (number.mod(THREE).equals(ZERO)) {
            return false;
        }

        final BigInteger numberSqrt = number.sqrt();
        for (BigInteger i = FIVE; i.compareTo(numberSqrt) <= 0; i = i.add(SIX)) {
            if (number.mod(i).equals(ZERO) || number.mod(i.add(TWO)).equals(ZERO)) {
                return false;
            }
        }

        return true;
    }
}