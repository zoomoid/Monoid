package synth.modulation;

import net.beadsproject.beads.core.UGen;

/**
 * Interface for all modulatable parameters.
 * Implementing classes are regarded "Modulators" meaning they actually calculate a function to affect the parameter.
 * See {@link LFO} and {@link Envelope} for examples
 */
public interface Modulatable {

    /**
     * MIDI noteOn trigger for modulatable parameters dependent on retrigger events(e.g. Envelopes)
     */
    void noteOn();

    /**
     * MIDI noteOff trigger for modulatable parameters dependent on retrigger events(e.g. Envelopes)
     */
    void noteOff();

    /**
     * Interface method for UGen.setValue(...). Implementations may differ, depending on what value represents
     * @param value abstract "value" of the modulated parameter
     */
    void setValue(float value);

    /**
     * Interface method for UGen.getValue(). Implementations may differ, {@link Modulatable#setValue(float value)}
     * @return float representation of the parameters values
     */
    float getValue();

    /**
     * Interface method for {@link UGen#getValue(int i, int j)}
     * @param i output channel
     * @param j output channel buffer index
     * @return value of buffer index
     */
    float getValue(int i, int j);

    /**
     * Interface method for UGen.update();
     */
    void update();

    /**
     * Interface method for object cloning
     * @return new instance with shared parameters like the referenced object
     */
    Modulatable clone();
}
