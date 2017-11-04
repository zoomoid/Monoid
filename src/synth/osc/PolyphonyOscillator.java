package synth.osc;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.MIDIUtils;
import synth.exceptions.UnsupportedUGenException;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;

public class PolyphonyOscillator extends UGen {

    /**
     * Oscillator array
     */
    SmartOscillator[] oscillators;
    /**
     * Iterator
     */
    private int currentOscillator;
    private static boolean randomPhaseOffset = true;

    private AudioContext ac;

    private RangeLimiter limiter;

    /**
     * Creates a PolyphonyOscillator
     */
    public PolyphonyOscillator(AudioContext ac, int numVoices, Buffer buffer, float[] frequencies, float[] gains, int[] unisonVoices, float[] unisonSpread, float[] unisonBlend){
        super(ac);
        this.ac = ac;
        this.oscillators = new SmartOscillator[numVoices];
        this.limiter = new RangeLimiter(ac, 2);
        for(int i = 0; i < numVoices; i++){
            oscillators[i] = new SmartOscillator(ac, frequencies[i], buffer, unisonVoices[i], unisonSpread[i], unisonBlend[i]);
            oscillators[i].setup();
            oscillators[i].start();
        }
        ac.start();
    }
    public void send(ShortMessage message, long timeStamp){
        int next = currentOscillator + 1 % oscillators.length;
        SmartOscillator current = oscillators[next];
        if(message.getCommand() == ShortMessage.NOTE_OFF){
            this.findAndToggle(message.getData1());
        } else {
            current.send(message, timeStamp);
        }
    }

    @Override
    public void calculateBuffer(){

    }

    private void findAndToggle(int midiKey){
        for(int i = 0; i < oscillators.length; i++){
            Oscillator o = oscillators[i];
            if(o.getMidiNote() == midiKey){
                o.pause(true);
                o.setFrequency(0f);
                return;
            }
        }
    }
}
