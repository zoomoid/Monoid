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
    private Waveform wave;
    /** To store the inverse of the sampling frequency. */
    private double one_over_sr;

    private SmartOscillator dependent;
    private WaveType waveType;

    public BasicOscillator(AudioContext ac){
        this(ac, 0f, Waveform.SINE);
    }

    public BasicOscillator(AudioContext ac, float frequency, Waveform wave){
        this(ac, new Static(ac, frequency), wave);
    }

    public BasicOscillator(AudioContext ac, Modulatable frequency, Waveform wave){
        super(ac, frequency);
        if(wave != null){
            this.wave = wave;
        } else {
            this.wave = Waveform.SINE;
        }
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.one_over_sr = 1f / context.getSampleRate();
    }

    public Waveform getWave(){
        return this.wave;
    }

    public BasicOscillator setWave(Waveform wave){
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
    public String getWaveformName() {
        return this.wave.getName();
    }

    @Override
    public BasicOscillator setWaveform(Waveform waveform){
        this.wave = wave;
        return this;
    }

    @Override
    public void calculateBuffer(){
        this.frequency.update();
        this.gain.update();
        for(int j = 0; j < bufferSize; j++){
            cP = (float)(((cP + this.frequency.getValue(0, j) * one_over_sr) % 1.f) + 1.f) % 1.f;
            float waveSample = this.wave.getBuffer().getValueFraction(cP);
            float gainSample = this.gain.getValue(0, j);
            this.bufOut[0][j] = gainSample * waveSample;
        }
        this.bufOut[1] = this.bufOut[0];
    }
}
