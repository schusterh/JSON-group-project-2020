package modell;

import java.util.HashMap;
import java.util.Optional;

public class Production {
    public Optional<HashMap<String, Integer>> produce;
    public Optional<HashMap<String, Integer>> consume;
    private int duration;

    public Production(Optional<HashMap<String, Integer>> produce, Optional<HashMap<String, Integer>> consume, int duration) {
        this.produce = produce;
        this.consume = consume;
        this.duration = duration;
    }

    public void setProduce(Optional<HashMap<String, Integer>> produce) {
        this.produce = produce;
    }

    public void setConsume(Optional<HashMap<String, Integer>> consume) {
        this.consume = consume;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Optional<HashMap<String, Integer>> getConsume() {
        return consume;
    }
}