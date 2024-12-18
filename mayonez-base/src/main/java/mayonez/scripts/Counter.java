package mayonez.scripts;

import mayonez.math.*;

/**
 * Manually tracks a changeable quantity. The counter can be given min and max
 * bounds, and detects when it has counted past the bounds using {@link #isAtMin}
 * or {@link #isAtMax}.
 *
 * @author SlavSquatSuperstar
 */
public class Counter {

    private Interval interval; // min and max
    private float value; // current value

    /**
     * Create a counter with the given interval and set its starting value.
     *
     * @param min        the lowest value the counter should reach
     * @param max        the highest value the counter should reach
     * @param startValue where the counter should begin
     */
    public Counter(float min, float max, float startValue) {
        this.interval = new Interval(min, max);
        value = startValue;
    }

    // Getters and Setters

    /**
     * Counts up or down by the given value.
     *
     * @param increment now much to count
     */
    public void count(float increment) {
        value += increment;
    }

    /**
     * Get the min value of the counter.
     *
     * @return the min value
     */
    public float getMin() {
        return interval.min;
    }

    /**
     * Get the max value of the counter.
     *
     * @return the max value
     */
    public float getMax() {
        return interval.max;
    }

    /**
     * Get the current value of the counter.
     *
     * @return the current value
     */
    public float getValue() {
        return value;
    }

    /**
     * Set the current value of the counter.
     *
     * @param value the current value
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Clamp the current value of counter between the min and max.
     */
    public void clampValue() {
        value = interval.clamp(value);
    }

    /**
     * Set the counter's min and max interval.
     *
     * @param min the lower bound
     * @param max the upper bound
     */
    public void setInterval(float min, float max) {
        this.interval = new Interval(min, max);
    }

    /**
     * Whether the counter has count down to its min value.
     *
     * @return if the counter is ready
     */
    public boolean isAtMin() {
        return value <= interval.min;
    }

    /**
     * Whether the counter has count up to its max value.
     *
     * @return if the counter is ready
     */
    public boolean isAtMax() {
        return value >= interval.max;
    }

    /**
     * Reset the counter to its min value.
     */
    public void resetToMin() {
        value = interval.min;
    }

    /**
     * Reset the counter to its max value.
     */
    public void resetToMax() {
        value = interval.max;
    }

    @Override
    public String toString() {
        return String.format("Counter (%.4f-%.4f, %.4f)",
                value, interval.min, interval.max);
    }

}
