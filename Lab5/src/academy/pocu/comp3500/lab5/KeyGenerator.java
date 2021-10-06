package academy.pocu.comp3500.lab5;

import java.math.BigInteger;
import java.util.Random;

public class KeyGenerator {
    private static final BigInteger BIG_0 = BigInteger.ZERO;
    private static final BigInteger BIG_1 = BigInteger.ONE;
    private static final BigInteger BIG_2 = BigInteger.TWO;
    private static final BigInteger BIG_3 = BigInteger.valueOf(3);
    private static final BigInteger BIG_4 = BigInteger.valueOf(4);
    private static final BigInteger BIG_5 = BigInteger.valueOf(5);
    private static final BigInteger BIG_7 = BigInteger.valueOf(7);
    private static final BigInteger BIG_11 = BigInteger.valueOf(11);
    private static final BigInteger BIG_13 = BigInteger.valueOf(13);
    private static final BigInteger BIG_17 = BigInteger.valueOf(17);
    private static final BigInteger BIG_19 = BigInteger.valueOf(19);
    private static final BigInteger BIG_23 = BigInteger.valueOf(23);
    private static final BigInteger BIG_29 = BigInteger.valueOf(29);
    private static final BigInteger BIG_31 = BigInteger.valueOf(31);
    private static final BigInteger BIG_37 = BigInteger.valueOf(37);
    private static final BigInteger BIG_41 = BigInteger.valueOf(41);
    private static final BigInteger BIG_61 = BigInteger.valueOf(61);
    private static final BigInteger BIG_73 = BigInteger.valueOf(73);
    private static final BigInteger BIG_1_662_803 = BigInteger.valueOf(1662803);

    private static final BigInteger BIG_1_000_000 = BigInteger.valueOf(1000000L);
    private static final BigInteger BIG_1_000_000_000 = BigInteger.valueOf(1000000000L);
    private static final BigInteger BIG_1_000_000_000_000 = BigInteger.valueOf(1000000000000L);

    private static final Random RANDOM = new Random();
    private static final int TEST_COUNT = 20;

    private static final BigInteger DETERMINISTIC_TEST_2_047 = BigInteger.valueOf(2047);
    private static final BigInteger DETERMINISTIC_TEST_1_373_653 = BigInteger.valueOf(1373653);
    private static final BigInteger DETERMINISTIC_TEST_9_080_191 = BigInteger.valueOf(9080191);
    private static final BigInteger DETERMINISTIC_TEST_25_326_001 = BigInteger.valueOf(25326001);
    private static final BigInteger DETERMINISTIC_TEST_3_215_031_751 = BigInteger.valueOf(3215031751L);
    private static final BigInteger DETERMINISTIC_TEST_4_759_123_141 = BigInteger.valueOf(4759123141L);
    private static final BigInteger DETERMINISTIC_TEST_1_122_004_669_633 = BigInteger.valueOf(1122004669633L);
    private static final BigInteger DETERMINISTIC_TEST_2_152_302_898_747 = BigInteger.valueOf(2152302898747L);
    private static final BigInteger DETERMINISTIC_TEST_3_474_749_660_383 = BigInteger.valueOf(3474749660383L);
    private static final BigInteger DETERMINISTIC_TEST_341_550_071_728_321 = BigInteger.valueOf(341550071728321L);
    private static final BigInteger DETERMINISTIC_TEST_3_825_123_056_546_413_051 = BigInteger.valueOf(3825123056546413051L);
    private static final BigInteger DETERMINISTIC_TEST_18_446_744_073_709_551_616 = BigInteger.valueOf(18446744073709L).multiply(BIG_1_000_000).add(BigInteger.valueOf(551616));
    private static final BigInteger DETERMINISTIC_TEST_318_665_857_834_031_151_167_461 = BigInteger.valueOf(318665857834031L).multiply(BIG_1_000_000_000).add(BigInteger.valueOf(151167461L));
    private static final BigInteger DETERMINISTIC_TEST_3_317_044_064_679_887_385_961_981 = BigInteger.valueOf(3317044064679L).multiply(BIG_1_000_000_000_000).add(BigInteger.valueOf(887385961981L));

    public static boolean isPrime(final BigInteger number) {
        if (number.signum() <= 0 || number.equals(BIG_1) || number.equals(BIG_4)) {
            return false;
        }

        if (number.equals(BIG_2) || number.equals(BIG_3)) {
            return true;
        }

        // n - 1 = d * 2 ^ r (r >= 1)
        BigInteger d = number.subtract(BIG_1);
        while (d.mod(BIG_2).equals(BIG_0)) {
            d = d.divide(BIG_2);
        }

        if (number.compareTo(DETERMINISTIC_TEST_3_317_044_064_679_887_385_961_981) >= 0) {
            for (int i = 0; i < TEST_COUNT; i++) {
                // a: [2, n − 2]
                final BigInteger upperLimit = number.subtract(BIG_4);
                BigInteger a;
                do {
                    a = new BigInteger(upperLimit.bitLength(), RANDOM);
                } while (a.compareTo(upperLimit) > 0);
                a = a.add(BIG_2);

                if (isCanPrime(number, d, a) == false) {
                    return false;
                }
            }

            return true;
        } else {
            if (number.compareTo(DETERMINISTIC_TEST_2_047) < 0) {
                // if n < 2,047, it is enough to test a = 2;
                return isCanPrime(number, d, BIG_2);
            } else if (number.compareTo(DETERMINISTIC_TEST_1_373_653) < 0) {
                // if n < 1,373,653, it is enough to test a = 2 and 3;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3);
            } else if (number.compareTo(DETERMINISTIC_TEST_9_080_191) < 0) {
                // if n < 9,080,191, it is enough to test a = 31 and 73;
                return isCanPrime(number, d, BIG_31)
                        && isCanPrime(number, d, BIG_73);
            } else if (number.compareTo(DETERMINISTIC_TEST_25_326_001) < 0) {
                // if n < 25,326,001, it is enough to test a = 2, 3, and 5;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5);
            } else if (number.compareTo(DETERMINISTIC_TEST_3_215_031_751) < 0) {
                // if n < 3,215,031,751, it is enough to test a = 2, 3, 5, and 7;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7);
            } else if (number.compareTo(DETERMINISTIC_TEST_4_759_123_141) < 0) {
                // if n < 4,759,123,141, it is enough to test a = 2, 7, and 61;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_61);
            } else if (number.compareTo(DETERMINISTIC_TEST_1_122_004_669_633) < 0) {
                // if n < 1,122,004,669,633, it is enough to test a = 2, 13, 23, and 1662803;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_13)
                        && isCanPrime(number, d, BIG_23)
                        && isCanPrime(number, d, BIG_1_662_803);
            } else if (number.compareTo(DETERMINISTIC_TEST_2_152_302_898_747) < 0) {
                // if n < 2,152,302,898,747, it is enough to test a = 2, 3, 5, 7, and 11;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11);
            } else if (number.compareTo(DETERMINISTIC_TEST_3_474_749_660_383) < 0) {
                // if n < 3,474,749,660,383, it is enough to test a = 2, 3, 5, 7, 11, and 13;
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11)
                        && isCanPrime(number, d, BIG_13);
            } else if (number.compareTo(DETERMINISTIC_TEST_341_550_071_728_321) < 0) {
                // if n < 341,550,071,728,321, it is enough to test a = 2, 3, 5, 7, 11, 13, and 17.
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11)
                        && isCanPrime(number, d, BIG_13)
                        && isCanPrime(number, d, BIG_17);
            } else if (number.compareTo(DETERMINISTIC_TEST_3_825_123_056_546_413_051) < 0) {
                // if n < 3,825,123,056,546,413,051, it is enough to test a = 2, 3, 5, 7, 11, 13, 17, 19, and 23.
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11)
                        && isCanPrime(number, d, BIG_13)
                        && isCanPrime(number, d, BIG_17)
                        && isCanPrime(number, d, BIG_19)
                        && isCanPrime(number, d, BIG_23);
            } else if (number.compareTo(DETERMINISTIC_TEST_18_446_744_073_709_551_616) < 0) {
                // if n < 18,446,744,073,709,551,616 = 2^64, it is enough to test a = 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, and 37.
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11)
                        && isCanPrime(number, d, BIG_13)
                        && isCanPrime(number, d, BIG_17)
                        && isCanPrime(number, d, BIG_19)
                        && isCanPrime(number, d, BIG_23)
                        && isCanPrime(number, d, BIG_29)
                        && isCanPrime(number, d, BIG_31)
                        && isCanPrime(number, d, BIG_37);
            } else if (number.compareTo(DETERMINISTIC_TEST_318_665_857_834_031_151_167_461) < 0) {
                // if n < 318,665,857,834,031,151,167,461, it is enough to test a = 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, and 37.
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11)
                        && isCanPrime(number, d, BIG_13)
                        && isCanPrime(number, d, BIG_17)
                        && isCanPrime(number, d, BIG_19)
                        && isCanPrime(number, d, BIG_23)
                        && isCanPrime(number, d, BIG_29)
                        && isCanPrime(number, d, BIG_31)
                        && isCanPrime(number, d, BIG_37);
            } else {
                assert (number.compareTo(DETERMINISTIC_TEST_3_317_044_064_679_887_385_961_981) < 0);
                // if n < 3,317,044,064,679,887,385,961,981, it is enough to test a = 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, and 41.
                return isCanPrime(number, d, BIG_2)
                        && isCanPrime(number, d, BIG_3)
                        && isCanPrime(number, d, BIG_5)
                        && isCanPrime(number, d, BIG_7)
                        && isCanPrime(number, d, BIG_11)
                        && isCanPrime(number, d, BIG_13)
                        && isCanPrime(number, d, BIG_17)
                        && isCanPrime(number, d, BIG_19)
                        && isCanPrime(number, d, BIG_23)
                        && isCanPrime(number, d, BIG_29)
                        && isCanPrime(number, d, BIG_31)
                        && isCanPrime(number, d, BIG_37)
                        && isCanPrime(number, d, BIG_41);
            }
        }
    }

    private static boolean isCanPrime(final BigInteger num, BigInteger d, final BigInteger a) {
        // Miller–Rabin test
        // https://www.geeksforgeeks.org/primality-test-set-3-miller-rabin/


        // Compute a^d % n
        BigInteger x = a.modPow(d, num);
        final BigInteger numSubOne = num.subtract(BIG_1);

        if (x.equals(BIG_1) || x.equals(numSubOne)) {
            return true;
        }

        // Keep squaring x while one of the
        // following doesn't happen
        // (i) d does not reach n-1
        // (ii) (x^2) % n is not 1
        // (iii) (x^2) % n is not n-1
        while (d.equals(numSubOne) == false) {
            x = x.modPow(BIG_2, num);
            d = d.multiply(BIG_2);

            if (x.equals(BIG_1)) {
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