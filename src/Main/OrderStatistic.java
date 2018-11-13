// Written by Ethan Binyaminov
package Main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public abstract class OrderStatistic {
    protected final static int[] RANGE = {5000, 8000, 10000}; //N
    protected final static int[] SIZES = {100, 300, 500, 1000, 2000, 4000}; //n
    protected final static int TRAILS_COUNT = 50;
    protected final static String TABLE_NAME = "results.csv";

    /**
     * Creates and returns an array of a specified size, filled with
     * random numbers ranging from 1 to array size, inclusively.
     * @param size size of array to create
     * @param bound inclusive upper limit of random number to generate for each element in the array.
     * @return Array of specified size, filled with random numbers
     * @throws Exception when size less than 1 or bound less than 2
     */
    protected static int [] randomFill(int size, int bound) throws ArrayIndexOutOfBoundsException {
        if (size < 1 || bound < 2) throw new ArrayIndexOutOfBoundsException();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++)
            // generate random int from 1 to size
            arr[i] = ThreadLocalRandom.current().nextInt(1, bound+1);
        return  arr;
    }

    // append or create and write data to a CSV file
    protected static final void printResults(double[][] results) throws IOException {
        // begin creating and printing file
        //PrintWriter pw = new PrintWriter(new File(TABLE_NAME));
        FileWriter fw = new FileWriter(TABLE_NAME, true);
        StringBuilder table = new StringBuilder();
        // the line.separator thing will ensure the correct newline specific to the system running this is appended
        table.append(System.getProperty("line.separator"));
        table.append("SIZES,");
        for (int n : SIZES) {
            table.append(n);
            table.append(",");
        }
        table.append(System.getProperty("line.separator"));
        StringBuilder[] trialsFormatted = new StringBuilder[RANGE.length];
        for (int i = 0; i < trialsFormatted.length; i++)
            trialsFormatted[i] = new StringBuilder(String.format("%d,", RANGE[i]));

        // using string.format to avoid printing scientific notation and needless zeros after decimal.
        for (int i = 0; i < RANGE.length; i++)
            for (int j = 0; j < SIZES.length; j++)
                trialsFormatted[i].append(String.format("%.0f,", results[i][j]));

        for (StringBuilder sb : trialsFormatted) {
            table.append(sb);
            table.append(System.getProperty("line.separator"));
        }
        fw.append(table.toString());
        fw.close();
    }

    /**
     * Test the order statistic methods speed under different
     * @return true if test results printed to file
     */
    public static boolean testAndPrint() {
        double[][] results = new double[RANGE.length][SIZES.length];
        // loop going through each SIZE
        for (int i = 0; i < RANGE.length; i++) {
            int range = RANGE[i];
            for (int j = 0; j < SIZES.length; j++) {
                int size = SIZES[j];
                long average = 0;

                // get average time
                for (int k = 0; k < TRAILS_COUNT; k++) {
                    int[] arr = randomFill(size, range);
                    int target = ThreadLocalRandom.current().nextInt(0, size);
                    long startTime = System.nanoTime();
                    orderStatisticPivot(arr, target);
                    long finishTime = System.nanoTime();
                    average += (finishTime - startTime);
                }
                average /= TRAILS_COUNT;
                results[i][j] = average;
            }
        }
        try {
            printResults(results);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static int orderStatisticPivot(int arr[], int k){
        // will use pivot sort or dual pivot sort depending on Java version
        Arrays.sort(arr);
        return arr[k];
    }

    protected static int OrderStatisticSelection(int[] arr, int targetIndex) {
        if (arr.length < 1 || targetIndex > arr.length - 1)
            return -1;
        for (int i = 0; i <= targetIndex; i++)
        {
            // Find the minimum element in unsorted array
            int minIndex = i;
            for (int j = i+1; j < arr.length; j++)
                if (arr[j] < arr[minIndex])
                    minIndex = j;

            // swap minimum with current index
            int temp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = temp;
        }
        return arr[targetIndex];
    }
}