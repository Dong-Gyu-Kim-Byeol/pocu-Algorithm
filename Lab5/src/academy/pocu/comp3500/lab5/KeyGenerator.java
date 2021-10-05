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
        final byte[] numberBytes = number.toByteArray();
        final short firstByte = (short) (numberBytes[0] & 0xff);
        final short lastByte = (short) (numberBytes[numberBytes.length - 1] & 0xff);

        if (numberBytes[0] < 0) {
            return false;
        }

        // number == 1
        if (numberBytes.length == 1 && firstByte == 1) {
            return false;
        }

        // number == 2
        if (numberBytes.length == 1 && firstByte == 2) {
            return true;
        }

        // number == 3
        if (numberBytes.length == 1 && firstByte == 3) {
            return true;
        }

        // number % 2 == 0
        if (lastByte % 2 == 0) {
            return false;
        }

        // number % 3 == 0
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