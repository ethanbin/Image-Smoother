package Main.OrderStatistic.Strategies;

import Main.OrderStatistic.OrderStatistic;
import Main.OrderStatistic.OrderStatisticBaseStrategy;

public class QuickSelectStrategy extends OrderStatisticBaseStrategy {
    @Override
    public int findKthSmallestInt(int[] arr, int k) {
        if (k >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(k)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (k < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(k)  + "th smallest number.");
        return quickSelect(arr, 0, arr.length - 1, k);
    }

    /**
     *
     * @param arr input
     * @param k the targeted smallest number at position k
     * @param start starting position of the arrray, inclusive
     * @param end ending position of the array, inclusive
     * @return value of k smallest number
     */
    private int quickSelect(int[] arr, int start, int end, int k){
        if (start == end)
            return arr[start];

        int pivot = partition(arr, start, end);

        if (pivot == k)
            return arr[pivot];
        else if (pivot > k)
            return quickSelect(arr, start, pivot-1, k);
        else
            return quickSelect(arr, pivot + 1, end, k);
    }

    /**
     * Partition a (sub)array using arr[end] as a pivot, where every value less than the pivot is on the left of
     * the pivot and every value greater on the right.
     * @param arr input
     * @param start inclusive starting position
     * @param end inclusive ending position
     * @return
     */
    private int partition(int[] arr, int start, int end){
        int pivotValue = arr[end];
        int j = start;
        for (int i = start; i < end; i++){
            if (arr[i] < pivotValue) {
                OrderStatistic.swap(arr, i, j);
                j++;
            }
        }
        // swap pivot with first element greater than pivot
        OrderStatistic.swap(arr, j, end);
        return j;
    }
}
