package Main.OrderStatistics;

import java.awt.*;

public abstract class OrderStatisticStrategy {
    protected String HEADER = "ORDER STATISTIC - N/A";

    abstract public int findInt(int [] arr, int k);

    public abstract Color findColor(Color [] arr, int k);

    public String getHEADER(){
        return HEADER;
    }
}
