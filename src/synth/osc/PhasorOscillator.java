package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Phasor;

import java.util.LinkedList;

public class PhasorOscillator extends Oscillator {

    // oscillator voices
    private LinkedList<Phasor> voices;

    // unison parameters
    private float unisonSpread;
    private double unisonSpreadFunction;
    private int unisonVoices;
    // blending ratio in [0,1]. 1 means full blend (all voices are equally load), near 0 means other voices muted
    private float unisonBlend;

    private float volumeFactor;

    public PhasorOscillator(AudioContext ac){
        this(ac, 440.0f, 1, 0f);
    }

    /**
     * Creates a single-voices phasor oscillator with parametric frequency
     * @param ac AudioContext
     */
    public PhasorOscillator(AudioContext ac, float frequency){
        this(ac, frequency, 1, 0f);
    }


    /**
     * Creates a single-voiced oscillator with unisonVoices and waveshapes from {@link Buffer}
     * @param ac AudioContext
     * @param frequency WaveOscillator frequency
     * @param voices Unison voices
     * @param spread Pitch spread
     */
    public PhasorOscillator(AudioContext ac, float frequency, int voices, float spread){
        // center frequency
        super(ac, frequency);
        this.unisonSpreadFunction = 1;
        this.unisonVoices = voices;
        this.unisonSpread = spread;
        this.unisonBlend = 1f/voices;
        this.volumeFactor = 1;
    }

    /**
     * Creates completely new WavePlayer objects with possibly changed parameters
     */
    @Override
    void createOscillator(){
        LinkedList<Phasor> list = new LinkedList<>();
        for(int i = 0; i < unisonVoices; i++){
            list.add(new Phasor(this.ac, 0f));
        }
        this.voices = list;
    }

    /**
     * Updates unison voices pitch with spread by a given exponent
     */
    @Override
    void updateFrequency(){
        if(unisonVoices <= 1){
            // to prevent division by zero, encapsulate this in own block
            voices.get(0).setFrequency(frequency);
        } else {
            // absolute starting point of the linear spread
            float x = frequency - unisonSpread;

            for(int i = 0; i < voices.size(); i++){
                Phasor e = voices.get(i);
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
    @Override
    public void patchOutputs(){
        Gain o = new Gain(ac, 1, volumeFactor*1f);
        if(unisonVoices > 2){
            Gain centerGain, unisonGain;
            centerGain = new Gain(ac, 1, (float)(1-0.5*unisonBlend));
            unisonGain = new Gain(ac,1, (float)(0.5*unisonBlend));
            if(voices.size() % 2 == 0){
                // there will be two center elements
                int center = (int)(Math.floor(voices.size() / 2));
                for(Phasor e : voices){
                    if(e == voices.get(center) || e == voices.get(center + 1)){
                        centerGain.addInput(e);
                    } else {
                        unisonGain.addInput(e);
                    }
                }
            } else {
                // there will be only one center elements
                int center = (int)(Math.floor(voices.size() / 2));
                for(Phasor e : voices){
                    if(e == voices.get(center)){
                        centerGain.addInput(e);
                    } else {
                        unisonGain.addInput(e);
                    }
                }
            }
            o.addInput(centerGain);
            o.addInput(unisonGain);
        } else {
            // blending option has no effect for less than three voices
            Gain allVoicesOut = new Gain(ac, 1, (1f/unisonVoices));
            for(Phasor e : voices){
                allVoicesOut.addInput(e);
            }
            o.addInput(allVoicesOut);
        }
        super.output.addInput(o);
    }

    /**
     * Sets the number of voices for the oscillator
     * @param voices amount of unsion voices
     */
    public void setUnisonVoices(int voices){
        this.unisonVoices = voices;
    }

    public int getUnisonVoices() {
        return unisonVoices;
    }

    public float getUnisonSpread() {
        return unisonSpread;
    }

    public float getUnisonBlend() {
        return unisonBlend;
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
    public LinkedList<Phasor> getVoices() {
        return voices;
    }

    /**
     * Sets the global volume factor of the oscillator
     * @param volumeFactor global volume factor in [0,1]
     */
    public void setVolumeFactor(float volumeFactor) {
        if(volumeFactor <= 1 && volumeFactor >= 0) this.volumeFactor = volumeFactor;
    }


    public void kill() {
        for (Phasor v : voices) {
            v.kill();
        }
        output.kill();
    }
    public void start(){
        this.pause(false);
    }

    private void pause(boolean paused){
        for(Phasor v : voices){
            v.pause(paused);
        }
        output.pause(paused);
    }

    public void pause(){
        this.pause(true);
    }
}
