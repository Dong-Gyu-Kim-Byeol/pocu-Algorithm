package academy.pocu.comp3500.lab5;

import java.math.BigInteger;

public class KeyGenerator {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FIVE = BigInteger.valueOf(5);
    private static final BigInteger SIX = BigInteger.valueOf(6);

    public static boolean isPrime(final BigInteger number) {
        if (number.compareTo(ONE) <= 0) {
            return false;
        }

        if (number.compareTo(TWO) == 0 || number.compareTo(THREE) == 0) {
            return true;
        }
        if (number.mod(TWO).compareTo(ZERO) == 0 || number.mod(THREE).compareTo(ZERO) == 0) {
            return false;
        }

        for (BigInteger i = FIVE; i.multiply(i).compareTo(number) <= 0; i = i.add(SIX)) {
            if (number.mod(i).compareTo(ZERO) == 0 || number.mod(i.add(TWO)).compareTo(ZERO) == 0) {
                return false;
            }
        }

        return true;
    }
}