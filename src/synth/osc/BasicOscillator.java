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
        this.cP = super.phase.getValue();
    }

    public BasicOscillator(AudioContext ac, Modulatable frequency, Buffer wave){
        super(ac, frequency);
        if(wave != null){
            this.wave = wave;
        } else {
            this.wave = Buffer.SINE;
        }
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.phase = new Static(ac, -1);
        this.one_over_sr = 1f / context.getSampleRate();
    }

    public BasicOscillator(AudioContext ac, float frequency, Buffer wave){
        this(ac, new Static(ac, frequency), wave);
    }

    BasicOscillator(AudioContext ac, SmartOscillator dependent, float frequency, Buffer wave){
        this(ac, frequency, wave);
        this.dependent = dependent;
        this.addDependent(this.dependent);
        this.isUnisonOscillator = false;
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
    public BasicOscillator setPhase(UGen phase){
        return this.setPhase(phase.getValue());
    }

    @Override
    public synchronized void calculateBuffer(){
        zeroOuts();
        this.frequency.update();
        this.gain.update();
        this.phase.update();
        for(int j = 0; j < bufferSize; j++){
            cP = (float)(((cP + frequency.getValue(0, j) * one_over_sr) % 1.f) + 1.f) % 1.f;
            for(int i = 0; i < outs; i++){
                bufOut[i][j] = gain.getValue(0, j) * this.wave.getValueFraction(cP);
            }
        }

    }
}
