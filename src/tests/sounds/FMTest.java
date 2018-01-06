package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import synth.modulation.ModulationOscillator;
import synth.osc.BasicOscillator;
import tests.ContextProvider;

public class FMTest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        BasicOscillator carrier = new BasicOscillator(ac, 440, Buffer.SINE);
        ModulationOscillator modulator = new ModulationOscillator(ac, ModulationOscillator.Type.SINE, 440, 220);
        modulator.noteOn();
        modulator.setValue(1204);
        modulator.setModulationStrength(400);
        carrier.setFrequency(modulator);
        Gain g = new Gain(ac, 2, 1f);
        g.addInput(carrier);
        ac.out.addInput(g);
    }
}
