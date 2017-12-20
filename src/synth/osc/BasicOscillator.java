package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.Static;
import net.beadsproject.beads.ugens.WavePlayer;

import javax.sound.midi.ShortMessage;

public class BasicOscillator extends Oscillator {

    /** The playback point in the Buffer, expressed as a fraction. */
    private double phase;

    /** The phase envelope. */
    private UGen phaseEnvelope;

    /** The Buffer. */
    private Buffer buffer;

    /** To store the inverse of the sampling frequency. */
    private float one_over_sr;

    private boolean isFreqStatic;

    public BasicOscillator(AudioContext ac){
        this(ac, 0f, Buffer.SINE);
    }

    public BasicOscillator(AudioContext ac, UGen frequency, Buffer wave){
        super(ac, frequency);
        this.buffer = wave;
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        phase = 0;
        one_over_sr = 1f / context.getSampleRate();
    }

    public BasicOscillator(AudioContext ac, float frequency, Buffer wave){
        this(ac, new Static(ac, frequency), wave);
    }

    public void setWave(Buffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void calculateBuffer(){
        frequency.update();
        if (phaseEnvelope == null) {
            for (int i = 0; i < bufferSize; i++) {
                phase = (((phase + frequency.getValue(0, i) * one_over_sr) % 1.0f) + 1.0f) % 1.0f;
                bufOut[0][i] = buffer.getValueFraction((float) phase);
                bufOut[1][i] = buffer.getValueFraction((float) phase);
            }
        } else {
            phaseEnvelope.update();
            for (int i = 0; i < bufferSize; i++) {
                bufOut[0][i] = buffer.getValueFraction(phaseEnvelope.getValue(0, i));
                bufOut[1][i] = buffer.getValueFraction(phaseEnvelope.getValue(0, i));
            }
        }
    }

    public void createOscillator(){

    }

    public void updateFrequency(){

    }

    public void patchOutput(){

    }

    /*
     * (non-Javadoc)
     * @see com.olliebown.beads.core.UGen#start()
     */
    public void start() {
        super.start();
        phase = 0;
    }

    /**
     * Gets the phase controller UGen, if there is one.
     * @return The phase controller UGen.
     */
    public UGen getPhaseUGen() {
        return phaseEnvelope;
    }

    /**
     * Gets the current phase;
     * @return The current phase.
     */
    public float getPhase() {
        return (float) phase;
    }

    /**
     * Sets a UGen to control the phase. This will override any frequency
     * controllers.
     * @param phaseController The new phase controller.
     * @return This WavePlayer instance.
     */
    public BasicOscillator setPhase(UGen phaseController) {
        this.phaseEnvelope = phaseController;
        if (phaseController != null) {
            phase = phaseController.getValue();
        }
        return this;
    }

    /**
     * Sets the phase. This will clear the phase controller UGen, if there is
     * one.
     * @param phase The new phase.
     * @return This WavePlayer instance.
     */
    public BasicOscillator setPhase(float phase) {
        this.phase = phase;
        this.phaseEnvelope = null;
        return this;
    }

    /**
     * Sets the Buffer.
     * @param b The new Buffer.
     */
    public BasicOscillator setBuffer(Buffer b) {
        this.buffer = b;
        return this;
    }

    /**
     * Gets the Buffer.
     * @return The Buffer.
     */
    public Buffer getBuffer() {
        return this.buffer;
    }

}
