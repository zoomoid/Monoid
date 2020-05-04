package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.filter.Filter;
import synth.filter.models.FilterModel;
import synth.osc.SmartOscillator;
import synth.auxilliary.ContextProvider;
import synth.osc.Waveform;

public class FilterTest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();
        SmartOscillator osc = new SmartOscillator(ac, 55f, Waveform.SAW);
        osc.setFrequency(55f);


        Filter filter = new Filter(ac, FilterModel.Type.BiquadFilter, FilterModel.Mode.LPF, 220, 24, 0.33f);

        filter.addInput(osc);

        ac.out.addInput(filter);
    }
}
