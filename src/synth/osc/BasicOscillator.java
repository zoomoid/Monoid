package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import synth.modulation.Modulatable;
import synth.modulation.Static;

public class BasicOscillator extends Oscillator implements WavetableOscillator {

    /* Phase sampler */
    private float cP;
    /** The Buffer. */
    private Buffer wave;
    /** To store the inverse of the sampling frequency. */
    private double one_over_sr;

    private SmartOscillator dependent;

    public BasicOscillator(AudioContext ac){
        this(ac, 0f, Buffer.SINE);
    }


    BasicOscillator(AudioContext ac, SmartOscillator dependent, Modulatable frequency, Buffer wave){
        this(ac, frequency, wave);
        this.dependent = dependent;
        this.addDependent(this.dependent);
        this.cP = 0;
    }

    public BasicOscillator(AudioContext ac, Modulatable frequency, Buffer wave){
        super(ac, frequency);
        if(wave != null){
            this.wave = wave;
        } else {
            this.wave = Buffer.SINE;
        }
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.one_over_sr = 1f / context.getSampleRate();
    }

    public BasicOscillator(AudioContext ac, float frequency, Buffer wave){
        this(ac, new Static(ac, frequency), wave);
    }

    BasicOscillator(AudioContext ac, SmartOscillator dependent, float frequency, Buffer wave){
        this(ac, frequency, wave);
        this.dependent = dependent;
        this.addDependent(this.dependent);
    }

    public Buffer getWave(){
        return this.wave;
    }

    public BasicOscillator setWave(Buffer wave){
        if(wave != null){
            this.wave = wave;
        }
        return this;
    }

    public final void setType(){
        if(this.type == null){
            this.type = "BasicOscillator";
        }
    }

    public BasicOscillator setPhase(float phase){
        this.cP = phase % 1.f;
        return this;
    }


    @Override
    public void calculateBuffer(){
        this.frequency.update();
        this.gain.update();
        for(int j = 0; j < bufferSize; j++){
            cP = (float)(((cP + this.frequency.getValue(0, j) * one_over_sr) % 1.f) + 1.f) % 1.f;
            float waveSample = this.wave.getValueFraction(cP);
            float gainSample = this.gain.getValue(0, j);
            for(int i = 0; i < outs; i++){
                this.bufOut[i][j] = gainSample * waveSample;
            }
        }

    }
}
