package academy.pocu.comp3500.lab5;

import java.math.BigInteger;

public class KeyGenerator {
    public static boolean isPrime(final BigInteger number) {
        final BigInteger zero = BigInteger.valueOf(0);
        final BigInteger one = BigInteger.valueOf(1);
        final BigInteger two = BigInteger.valueOf(2);
        final BigInteger three = BigInteger.valueOf(3);
        final BigInteger five = BigInteger.valueOf(3);
        final BigInteger six = BigInteger.valueOf(3);

        if (number.compareTo(one) <= 0) {
            return false;
        }

        if (number.compareTo(two) == 0 || number.compareTo(three) == 0) {
            return true;
        }
        if (number.mod(two).compareTo(zero) == 0 || number.mod(three).compareTo(zero) == 0) {
            return false;
        }

        for (BigInteger i = five; i.multiply(i).compareTo(number) <= 0; i = i.add(six)) {
            if (number.mod(i).compareTo(zero) == 0 || number.mod(i.add(two)).compareTo(zero) == 0) {
                return false;
            }
        }

        return true;
    }


}