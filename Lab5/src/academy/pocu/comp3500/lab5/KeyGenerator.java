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

        if (number.equals(TWO) || number.equals(THREE)) {
            return true;
        }

        if (number.equals(ONE) || number.mod(TWO).equals(ZERO) || number.mod(THREE).equals(ZERO)) {
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