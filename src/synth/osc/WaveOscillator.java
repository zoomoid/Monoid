package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.WavePlayer;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;

import java.util.LinkedList;

public class WaveOscillator extends Oscillator {

    // oscillator voices
    private LinkedList<WavePlayer> voices;

    private AudioContext ac;

    // oscillator waveshape
    private Buffer waveshape;
    // oscillator frequency
    private float frequency;
    // unison parameters
    private float unisonSpread;
    private double unisonSpreadFunction;
    private int unisonVoices;
    // blending ratio in [0,1]. 1 means full blend (all voices are equally load), near 0 means other voices muted
    private float unisonBlend;
    private float volumeFactor;

    // master output gain
    protected Gain out;

    //public static boolean RANDOM_PHASE_OFFSET = true;

    /**
     * Default constructor.
     * Creates a single-voices A4 sine oscillator
     * @param ac AudioContext
     */


    public WaveOscillator(AudioContext ac){
        this(ac, 440.0f, Buffer.SINE, 1, 0f);
    }

    /**
     * Creates a single-voices sine oscillator with parametric frequency
     * @param ac AudioContext
     */
    public WaveOscillator(AudioContext ac, float frequency){
        this(ac, frequency, Buffer.SINE, 1, 0f);
    }

    /**
     * Creates a single-voiced oscillator with waveshapes from {@link Buffer}
     * @param ac AudioContext
     * @param frequency WaveOscillator frequency
     * @param waveshape Waveshape buffer
     */
    public WaveOscillator(AudioContext ac, float frequency, Buffer waveshape){
        this(ac, frequency, waveshape, 1, 0f);
    }

    /**
     * Creates a single-voiced oscillator with unisonVoices and waveshapes from {@link Buffer}
     * @param ac AudioContext
     * @param frequency WaveOscillator frequency
     * @param waveshape Waveshape buffer
     * @param voices Unison voices
     * @param spread Pitch spread
     */
    public WaveOscillator(AudioContext ac, float frequency, Buffer waveshape, int voices, float spread){
        // center frequency
        super(ac, frequency);
        this.unisonSpreadFunction = 1;
        this.ac = ac;
        this.frequency = frequency;
        this.waveshape = waveshape;
        this.unisonVoices = voices;
        this.unisonSpread = spread;
        this.unisonBlend = 1f/voices;
        this.volumeFactor = 1;
        this.createOscillator();
    }

    /**
     * Creates completely new WavePlayer objects with possibly changed parameters
     */
    void createOscillator(){
        this.createVoices();
        this.updateFrequency();
        this.patchOutputs();
    }

    /**
     * Wether the WaveOscillator only has a single centerVoice or is a unisonVoices oscillator
     * @return isSingleVoiced
     */
    public boolean isSingleVoiced() {
        return unisonVoices <= 1;
    }

    public void createVoices(){
        LinkedList<WavePlayer> list = new LinkedList<>();
        for(int i = 0; i < unisonVoices; i++){
            list.add(new WavePlayer(this.ac, 0f, this.waveshape));
        }
        this.voices = list;
    }

    /**
     * Updates unison voices pitch with spread by a given exponent
     */
    public void updateFrequency(){
        if(unisonVoices <= 1){
            // to prevent division by zero, encapsulate this in own block
            voices.get(0).setFrequency(frequency);
        } else {
            // absolute starting point of the linear spread
            float x = frequency - unisonSpread;

            for(int i = 0; i < voices.size(); i++){
                WavePlayer e = voices.get(i);
                float frequency = (float)(x + (i/Math.pow(unisonVoices-1f, unisonSpreadFunction)) * 2 * unisonSpread);
                e.setFrequency(frequency);
                e.setPhase((float)(Math.random()*2*Math.PI*frequency));
            }
        }
    }

    /**
     * Patches Voices to Gains by a given blending ratio
     * Note that for unisonVoices <= 2, this ratio will have no effects, voices will be blended equally
     */
    public void patchOutputs(){
        this.out = new Gain(ac, 1, volumeFactor*1f);
        if(unisonVoices > 2){
            Gain centerGain, unisonGain;
            centerGain = new Gain(ac, 1, (float)(1-0.5*unisonBlend));
            unisonGain = new Gain(ac,1, (float)(0.5*unisonBlend));
            if(voices.size() % 2 == 0){
                // there will be two center elements
                int center = (int)(Math.floor(voices.size() / 2));
                for(WavePlayer e : voices){
                    if(e == voices.get(center) || e == voices.get(center + 1)){
                        centerGain.addInput(e);
                    } else {
                        unisonGain.addInput(e);
                    }
                }
            } else {
                // there will be only one center elements
                int center = (int)(Math.floor(voices.size() / 2));
                for(WavePlayer e : voices){
                    if(e == voices.get(center)){
                        centerGain.addInput(e);
                    } else {
                        unisonGain.addInput(e);
                    }
                }
            }
            this.out.addInput(centerGain);
            this.out.addInput(unisonGain);
        } else {
            // blending option has no effect for less than three voices
            Gain allVoicesOut = new Gain(ac, 1, (1f/unisonVoices));
            for(WavePlayer e : voices){
                allVoicesOut.addInput(e);
            }
            this.out.addInput(allVoicesOut);
        }

    }

    /**
     * Returns the master output object
     * @return Master output
     */
    public Gain output(){
        return this.out;
    }

    /**
     * Sets the master output gain of the oscillator to a number between 0 and 1
     * @param gain output gain in [0,1]
     */
    public void setOutputGain(float gain){
        if(gain >= 0f && gain <= 1f){
            this.out.setGain(volumeFactor*gain);
        }
    }

    /**
     * Sets the number of voices for the oscillator
     * @param voices amount of unsion voices
     */
    public void setUnisonVoices(int voices){
        this.unisonVoices = voices;
        this.createVoices();
        this.updateFrequency();
        this.patchOutputs();
    }

    public int getUnisonVoices() {
        return unisonVoices;
    }

    public float getUnisonSpread() {
        return unisonSpread;
    }

    /**
     * Adjusts the pitch spread of the unison voices
     * @param spread frequency spread to one side
     * @param fn exponent of the reciprocal pitch calculation
     */
    public void setUnisonSpread(float spread, double fn){
        this.unisonSpread = spread;
        this.unisonSpreadFunction = fn;
        this.updateFrequency();
    }

    /**
     * Sets the unisonVoices spread ratio
     * @param spread unisonVoices pitch spread
     */
    public void setUnisonSpread(float spread){
        this.setUnisonSpread(spread, 1);
    }



    /**
     * Sets the unisonBlend ratio
     * Blending ratio in [0,1]. 1 means full blend (all voices are equally load), near 0 means other voices muted
     * @param unisonBlend new unison blend ratio in [0,1]
     */
    public void setUnisonBlend(float unisonBlend) {
        if(unisonBlend >= 0 && unisonBlend <= 1) this.unisonBlend = unisonBlend;
        this.patchOutputs();
    }

    public float getUnisonBlend() {
        return unisonBlend;
    }

    /**
     * Sets the WaveShape of the WaveOscillator
     * @param waveshape {@link Buffer}
     */
    public void setWaveshape(Buffer waveshape) {
        this.waveshape = waveshape;
        // update all unisonVoices voices as well
        for(WavePlayer u : voices){
            u.setBuffer(waveshape);
        }
    }

    /**
     * Get the WaveShape of the WaveOscillator
     * @return The serialized wave type buffer. Without a strong visualisation ability you won't be able to recognize the wave shape from the buffer
     */
    public Buffer getWaveshape() {
        return waveshape;
    }

    /**
     * Sets the frequency of the WaveOscillator.
     * @param frequency base frequency of the oscillator
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
        this.updateFrequency();
    }

    /**
     * Get the oscillator's frequency
     * @return oscillator frequency
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * Get the Unison Voices
     * @return unisonVoices
     */
    public LinkedList<WavePlayer> getVoices() {
        return voices;
    }

    /**
     * Sets the global volume factor of the oscillator
     * @param volumeFactor global volume factor in [0,1]
     */
    public void setVolumeFactor(float volumeFactor) {
        if(volumeFactor <= 1 && volumeFactor >= 0) this.volumeFactor = volumeFactor;
    }

    public void kill(){
        for(WavePlayer p : voices){
            p.kill();
        }
        output.kill();
        isPaused = true;
    }

    public void start(){
        this.pause(false);
    }

    private void pause(boolean paused){
        for(WavePlayer p : voices){
            p.pause(paused);
        }
        output.pause(paused);
        isPaused = paused;
    }

    public void pause(){
        this.pause(true);
    }
}
