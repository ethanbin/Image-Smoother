package Main.OrderStatistic.Strategies;

import Main.OrderStatistic.OrderStatisticTester;
import Main.OrderStatistic.OrderStatisticStrategy;

import java.awt.*;

public class QuickSelectStrategy extends OrderStatisticStrategy {
    public QuickSelectStrategy() {
        this.HEADER = "ORDER STATISTIC - QUICK SELECT";
    }

    @Override
    public int findInt(int[] arr, int k) {
        if (k >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(k)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (k < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(k)  + "th smallest number.");
        return quickSelect(arr, 0, arr.length - 1, k);
    }

    @Override
    public Color findColor(Color [] arr, int k){
        if (k >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(k)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (k < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(k)  + "th smallest number.");
        return findColor(arr, 0, arr.length-1, k);
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
                int tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                j++;
            }
        }
        // swap pivot with first element greater than pivot
        int tmp = arr[j];
        arr[j] = arr[end];
        arr[end] = tmp;
        return j;
    }

    // start and end are inclusive
    private Color findColor(Color [] arr, int start, int end, int k){
        if (start == end)
            return arr[start];

        int pivot = partition(arr, start, end);

        if (pivot == k)
            return arr[pivot];
        else if (pivot > k)
            return findColor(arr, start, pivot-1, k);
        else
            return findColor(arr, pivot + 1, end, k);

    }
    
    private int partition(Color [] arr, int start, int end){
        Color pivotColor = arr[end];
        int pivotGreyLevelValue = (pivotColor.getRed() + pivotColor.getGreen() + pivotColor.getBlue()) / 3;
        int j = start;
        for (int i = start; i < end; i++){
            Color currentColor = arr[i];
            int currentGreyLevel = (currentColor.getRed() + currentColor.getGreen() + currentColor.getBlue()) / 3;
            if (currentGreyLevel < pivotGreyLevelValue) {
                arr[i] = arr[j];
                arr[j] = currentColor;
                j++;
            }
        }
        // swap pivot with first element greater than pivot
        arr[end] = arr[j];
        arr[j] = pivotColor;
        return j;
    }
}
