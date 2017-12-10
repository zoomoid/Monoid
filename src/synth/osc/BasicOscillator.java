package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.WavePlayer;

import javax.sound.midi.ShortMessage;

public class BasicOscillator extends Oscillator {

    private WavePlayer osc;

    private Buffer wave;

    public BasicOscillator(AudioContext ac){
        this(ac, 0f, Buffer.SINE);
    }

    public BasicOscillator(AudioContext ac, float frequency, Buffer wave){
        super(ac, frequency);
        this.wave = wave;
        osc = new WavePlayer(ac, frequency, this.wave);
        this.outputInitializationRegime = OutputInitializationRegime.RETAIN;
        bufOut = bufIn;
    }

    public void setFrequency(float frequency){
        this.updateFrequency();
    }

    @Override
    public void calculateBuffer(){
        for(int i = 0; i < outs; i++){
            bufOut[i] = output.getOutBuffer(i);
        }
    }

    public void createOscillator(){}

    public void updateFrequency(){}

    public void patchOutput(){}

    public void send(ShortMessage message, long timeStamp){
        if(message.getCommand() == ShortMessage.NOTE_OFF){
            this.noteOff();
        } else {
            // call for the static function translating the MIDI key to frequency range
            this.setFrequency(Pitch.mtof(message.getData1()));
            this.setMidiNote(message.getData1());
            // if oscillator is velocity sensitive, adjust volume
            if(this.isVelocitySensitive){
                this.output.setGain(message.getData2() / 127f * this.output.getGain());
            }
            this.noteOn();
        }
    }
}
