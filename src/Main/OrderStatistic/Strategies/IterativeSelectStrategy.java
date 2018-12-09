package Main.OrderStatistic.Strategies;
import Main.OrderStatistic.OrderStatisticTester;
import Main.OrderStatistic.OrderStatisticStrategy;

import java.awt.*;

public class IterativeSelectStrategy extends OrderStatisticStrategy {
    final String HEADER = "ORDER STATISTIC - ITERATIVE SELECTION";

    @Override
    public int findInt(int [] arr, int targetIndex){
        if (targetIndex >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(targetIndex)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (targetIndex < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(targetIndex)  + "th smallest number.");

        for (int i = 0; i <= targetIndex; i++)
        {
            // Find the minimum element in unsorted array
            int minIndex = i;
            for (int j = i+1; j < arr.length; j++)
                if (arr[j] < arr[minIndex])
                    minIndex = j;

            // swap minimum with current index
            int tmp = arr[minIndex];
            arr[minIndex] = arr[i];
            arr[i] = tmp;
        }
        return arr[targetIndex];

    }

    @Override
    public Color findColor(Color[] arr, int k) {
        if (k >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(k)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (k < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(k)  + "th smallest number.");

        // selection sort iteratively
        for (int i = 0; i <= k; i++) {
            // Find the minimum element in unsorted array
            int minIndex = i;
            for (int j = i+1; j < arr.length; j++) {
                int jGray = (arr[j].getRed() + arr[j].getBlue() + arr[j].getGreen()) / 3;
                int minGray = (arr[minIndex].getRed() + arr[minIndex].getBlue() + arr[minIndex].getGreen()) / 3;
                if (jGray < minGray)
                    minIndex = j;
            }

            // Swap the found minimum element with the first
            // element
            Color temp = new Color(arr[minIndex].getRGB());
            arr[minIndex] = new Color(arr[i].getRGB());
            arr[i] = new Color(temp.getRGB());
        }

        return arr[k];
    }
}
