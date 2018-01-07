package synth.modulation;

import net.beadsproject.beads.core.AudioContext;

/**
 * NormLFO does not use the shift / center value field to be used in {@link Sum}
 */
public class NormLFO extends LFO {

    public NormLFO(AudioContext ac){
        this(ac, Type.SINE, 1f, 1f);
    }

    public NormLFO(AudioContext ac, Type type, float frequency, float amplitude){
        this(ac, type, Mode.RETRIGGER, frequency, amplitude);
    }

    public NormLFO(AudioContext ac, Type type, Mode mode, float frequency, float amplitude){
        super(ac, type, mode, frequency, amplitude);
    }

    /**
     * TODO test implementation
     */
    @Override
    public void calculateBuffer(){
        // since frequency is (pseudo) static, we can save up calculations by doing this once
        double increment = frequency * one_over_sr;
        for (int i = 0; i < bufferSize; i++) {
            // current phase cache
            phase = (((phase + increment) % 1.0f) + 1.0f) % 1.0f;
            // since the lfo only has one output channel
            bufOut[0][i] = gate * modulationStrength * buffer.getValueFraction((float) phase);
        }
    }
}
