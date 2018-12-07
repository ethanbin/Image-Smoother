package Main.OrderStatistic.Strategies;

import Main.OrderStatistic.OrderStatisticBaseStrategy;

import java.util.Arrays;

public class QuickSortStrategy extends OrderStatisticBaseStrategy {
    @Override
    public int findKthSmallestInt(int [] arr, int k){
        if (k >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(k)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (k < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(k)  + "th smallest number.");
        // will use pivot sort or dual pivot sort depending on Java version
        Arrays.sort(arr);
        return arr[k];
    }
}
