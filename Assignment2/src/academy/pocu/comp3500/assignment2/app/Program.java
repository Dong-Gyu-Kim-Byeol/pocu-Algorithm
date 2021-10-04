package academy.pocu.comp3500.assignment2.app;

import academy.pocu.comp3500.assignment2.Logger;

import static academy.pocu.comp3500.assignment2.Logger.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Program {
    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("mylog1.log"));

        int[] nums = new int[]{1, 2, 3, 4};

        log("call sum()");
        int sum = sum(nums);

        log("call average()");
        double average = calculateAverage(nums);

        Logger.printTo(writer);
        /*
        call sum()
        sum + 1
        sum: 1
        sum + 2
        sum: 3
        sum + 3
        sum: 6
        sum + 4
        sum: 10
        return sum: 10
        call average()
        call sum()
        sum + 1
        sum: 1
        sum + 2
        sum: 3
        sum + 3
        sum: 6
        sum + 4
        sum: 10
        return sum: 10
        sum / nums.length: 10 / 4
        return average: 2.500000
        */

        writer.close();
    }

    private static int sum(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; ++i) {
            log(String.format("sum + %d", nums[i]));
            sum += nums[i];
            log(String.format("sum: %d", sum));
        }

        log(String.format("return sum: %d", sum));
        return sum;
    }

    private static double calculateAverage(int[] nums) {
        log("call sum()");
        int sum = sum(nums);

        log(String.format("sum / nums.length: %d / %d", sum, nums.length));
        double average = sum / (double) nums.length;

        log(String.format("return average: %f", average));
        return average;
    }
}
