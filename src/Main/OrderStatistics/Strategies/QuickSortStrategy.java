package Main.OrderStatistics.Strategies;

import Main.OrderStatistics.OrderStatisticStrategy;

import java.awt.*;
import java.util.Arrays;

public class QuickSortStrategy extends OrderStatisticStrategy {
    public QuickSortStrategy() {
         super.HEADER = "ORDER STATISTIC - QUICK SORT";
    }

    @Override
    public int findInt(int [] arr, int k){
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

    @Override
    public Color findColor(Color[] arr, int k) {
        if (k >= arr.length)
            throw new ArrayIndexOutOfBoundsException("Requested the " + Integer.toString(k)  + "th " +
                    "smallest number but the array is only of size " + Integer.toString(arr.length) + ".");
        else if (k < 0)
            throw new ArrayIndexOutOfBoundsException("Target value cannot be negative - user requested the " +
                    Integer.toString(k)  + "th smallest number.");

        quickSort(arr, 0, arr.length-1);
        return arr[k];
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

    private void quickSort(Color[] arr, int start, int end){
        if (start < end){
            int pivot = partition(arr, start, end);
            quickSort(arr, start, pivot-1);
            quickSort(arr,pivot+1, end);
        }
    }
}