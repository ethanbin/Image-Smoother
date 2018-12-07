package Main.OrderStatistic.Strategies;
import Main.OrderStatistic.OrderStatistic;
import Main.OrderStatistic.OrderStatisticBaseStrategy;

public class IterativeSelectStrategy extends OrderStatisticBaseStrategy {
    @Override
    public int findKthSmallestInt(int [] arr, int targetIndex){
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
            OrderStatistic.swap(arr, minIndex, i);
        }
        return arr[targetIndex];

    }

}