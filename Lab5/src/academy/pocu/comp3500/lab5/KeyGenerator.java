package academy.pocu.comp3500.lab5;

import java.math.BigInteger;

public class KeyGenerator {
    public static boolean isPrime(final BigInteger number) {
        if (number.mod(BigInteger.valueOf(2)).compareTo(BigInteger.valueOf(0)) == 0) {
            return number.compareTo(BigInteger.valueOf(2)) == 0;
        } else if (number.mod(BigInteger.valueOf(3)).compareTo(BigInteger.valueOf(0)) == 0) {
            return number.compareTo(BigInteger.valueOf(3)) == 0;
        } else if (number.mod(BigInteger.valueOf(5)).compareTo(BigInteger.valueOf(0)) == 0) {
            return number.compareTo(BigInteger.valueOf(5)) == 0;
        } else if (number.mod(BigInteger.valueOf(7)).compareTo(BigInteger.valueOf(0)) == 0) {
            return number.compareTo(BigInteger.valueOf(7)) == 0;
        }

        return false;
    }


}