package academy.pocu.comp3500.lab5;

import java.math.BigInteger;
import java.util.Random;

public class KeyGenerator {
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.TWO;
    private static final BigInteger THREE = BigInteger.valueOf(3);
    private static final BigInteger FOUR = BigInteger.valueOf(4);
    private static final Random RANDOM = new Random();
    private static final int TEST_COUNT = 20;

    public static boolean isPrime(final BigInteger number) {
        if (number.signum() <= 0 || number.equals(ONE) || number.equals(FOUR)) {
            return false;
        }

        if (number.equals(TWO) || number.equals(THREE)) {
            return true;
        }

        // n - 1 = d * 2 ^ r (r >= 1)
        BigInteger d = number.subtract(ONE);
        while (d.mod(TWO).equals(ZERO)) {
            d = d.divide(TWO);
        }

        for (int i = 0; i < TEST_COUNT; i++) {
            if (isCanPrime(number, d) == false) {
                return false;
            }
        }

        return true;
    }

    private static boolean isCanPrime(final BigInteger num, BigInteger d) {
        // Miller–Rabin test
        // https://www.geeksforgeeks.org/primality-test-set-3-miller-rabin/

        // a: [2, n − 2]
        final BigInteger upperLimit = num.subtract(FOUR);
        BigInteger a;
        do {
            a = new BigInteger(upperLimit.bitLength(), RANDOM);
        } while (a.compareTo(upperLimit) > 0);
        a = a.add(TWO);

        // Compute a^d % n
        BigInteger x = a.modPow(d, num);
        final BigInteger numSubOne = num.subtract(ONE);

        if (x.equals(ONE) || x.equals(numSubOne)) {
            return true;
        }

        // Keep squaring x while one of the
        // following doesn't happen
        // (i) d does not reach n-1
        // (ii) (x^2) % n is not 1
        // (iii) (x^2) % n is not n-1
        while (d.equals(numSubOne) == false) {
            x = x.modPow(TWO, num);
            d = d.multiply(TWO);

            if (x.equals(ONE)) {
                return false;
            }
            if (x.equals(numSubOne)) {
                return true;
            }
        }

        // Return composite
        return false;
    }
}