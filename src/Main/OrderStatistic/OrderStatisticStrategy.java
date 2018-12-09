package Main.OrderStatistic;

import java.awt.*;

public abstract class OrderStatisticStrategy {
    final String HEADER = "ORDER STATISTIC - QUICK SORT";

    abstract public int findInt(int [] arr, int k);

    public abstract Color findColor(Color [] arr, int k);


    public String getHEADER(){
        return HEADER;
    }
}
