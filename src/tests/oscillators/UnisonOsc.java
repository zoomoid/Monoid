package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.auxilliary.MIDIUtils;
import synth.osc.UnisonOscillator;
import tests.ContextProvider;

public class UnisonOsc {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        UnisonOscillator osc = new UnisonOscillator(ac, Buffer.SQUARE, 5);
        float[] frequencies = {MIDIUtils.midi2frequency(48+3), MIDIUtils.midi2frequency(52+3), MIDIUtils.midi2frequency(55+3), MIDIUtils.midi2frequency(59+3), MIDIUtils.midi2frequency(59+3+3)};
        float[] gains = {0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
        osc.setFrequencies(frequencies);
        osc.setGains(gains);

        ac.out.addInput(osc);
    }
}
