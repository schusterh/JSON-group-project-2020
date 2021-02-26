package modell;

import java.util.ArrayList;

public class GoodsBundle {
    private String goodType;
    private int goodAmount;
    private Station targetStation;

    public GoodsBundle(String goodType, int goodAmount, Station targetStation){
        this.goodType = goodType;
        this.goodAmount = goodAmount;
        this.targetStation = targetStation;
    }

    public Station getTargetStation() {
        return targetStation;
    }

    public void setTargetStation(Station targetStation) {
        this.targetStation = targetStation;
    }

    public String getGoodType() {
        return goodType;
    }

    public int getGoodAmount() {
        return goodAmount;
    }

    public void setGoodAmount(int goodAmount) {
        this.goodAmount = goodAmount;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }
}
