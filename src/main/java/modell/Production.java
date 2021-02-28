package modell;

import java.util.HashMap;
import java.util.Optional;

/**
 * The type Production.
 */
public class Production {
    /**
     * The Produce.
     */
    public Optional<HashMap<String, Integer>> produce;
    /**
     * The Consume.
     */
    public Optional<HashMap<String, Integer>> consume;
    private int duration;

    /**
     * Instantiates a new Production.
     *
     * @param produce  the produce
     * @param consume  the consume
     * @param duration the duration
     */
    public Production(Optional<HashMap<String, Integer>> produce, Optional<HashMap<String, Integer>> consume, int duration) {
        this.produce = produce;
        this.consume = consume;
        this.duration = duration;
    }

    /**
     * Sets produce.
     *
     * @param produce the produce
     */
    public void setProduce(Optional<HashMap<String, Integer>> produce) {
        this.produce = produce;
    }

    /**
     * Sets consume.
     *
     * @param consume the consume
     */
    public void setConsume(Optional<HashMap<String, Integer>> consume) {
        this.consume = consume;
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Gets consume.
     *
     * @return the consume
     */
    public Optional<HashMap<String, Integer>> getConsume() {
        return consume;
    }
}