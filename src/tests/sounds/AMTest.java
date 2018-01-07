package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import synth.modulation.ModulationOscillator;
import synth.osc.BasicOscillator;
import synth.auxilliary.ContextProvider;

public class AMTest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        BasicOscillator carrier = new BasicOscillator(ac, 440, Buffer.SINE);
        ModulationOscillator modulator = new ModulationOscillator(ac, ModulationOscillator.Type.SINE, 55f, 1f);
        modulator.noteOn();
        modulator.setModulationStrength(1f);
        carrier.setGain(modulator);
        Gain g = new Gain(ac, 2, 0.25f);
        g.addInput(carrier);
        ac.out.addInput(g);
    }
}
