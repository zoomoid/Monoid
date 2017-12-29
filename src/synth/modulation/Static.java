package synth.modulation;

import net.beadsproject.beads.core.AudioContext;

public class Static extends Modulator {
    /** The stored value. */
    public float x;

    /**
     * Instantiates a new Static with the given value.
     *
     * @param context
     *            the AudioContext.
     * @param x
     *            the value.
     */
    public Static(AudioContext context, float x) {
        super(context);
        this.x = x;
        outputInitializationRegime = OutputInitializationRegime.NULL;
        outputPauseRegime = OutputPauseRegime.NULL;
        pause(true); // might as well be muted
    }

    @Override
    public void calculateBuffer() {
        // Do nothing
    }

    public void setValue(float value) {
        x = value;
    }

    public float getValue(int a, int b) {
        return x;
    }

    public float getValue() {
        return x;
    }

    public Static clone(){
        return new Static(this.context, this.x);
    }

    public void noteOn(){
        // no-op
    }

    public void noteOff(){
        // no-op
    }
}
