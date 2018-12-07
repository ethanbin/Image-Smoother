package Main.OrderStatistic;

public abstract class OrderStatisticBaseStrategy {
    final String HEADER = "ORDER STATISTIC - QUICK SORT";

    abstract public int findKthSmallestInt(int [] arr, int k);

    public String getHEADER(){
        return HEADER;
    }
}
