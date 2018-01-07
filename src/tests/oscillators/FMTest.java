package tests.oscillators;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.WavePlayer;
import synth.modulation.ModulationOscillator;
import synth.auxilliary.ContextProvider;

public class FMTest {

    public static boolean FM_BY_MODULATOR = false;

    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        WavePlayer osc = new WavePlayer(ac, 0f, Buffer.SINE);
        ac.out.addInput(osc);
        ac.start();
        ac.out.setGain(0.1f);
        if(FM_BY_MODULATOR){
            ModulationOscillator fmOsc = new ModulationOscillator(ac, ModulationOscillator.Type.SINE, ModulationOscillator.Mode.FREE,444f, 1);
            // modulator specific sets
            fmOsc.setCenterValue(444f);
            fmOsc.setModulationStrength(444f);
            osc.setFrequency(fmOsc);
        } else {
            osc.setFrequency(new WavePlayer(ac, 111f, Buffer.SINE));
        }
    }

}
