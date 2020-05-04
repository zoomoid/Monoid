package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Pitch;
import synth.filter.Filter;
import synth.filter.models.FilterModel;
import synth.osc.SmartOscillator;
import synth.osc.Waveform;
import synth.ui.FilterUI;
import synth.ui.OscillatorUI;
import synth.auxilliary.ContextProvider;

public class FilterUITest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        SmartOscillator osc = new SmartOscillator(ac);
        osc.setFrequency(Pitch.mtof(48));
        osc.setBlend(2);
        osc.setSpread(0.25f);
        osc.setVoices(5);
        osc.setWave(Waveform.SAW);
        osc.setName("Oscillator A");

        Filter f = new Filter(ac, FilterModel.Type.BiquadFilter, FilterModel.Mode.LPF, 300f, 0.76f, 0.71f);
        f.setName("Filter A");
        f.addInput(osc);

        ac.out.addInput(f);
        ac.out.setGain(0.66f);
        OscillatorUI oUI = new OscillatorUI(osc);
        oUI.show();

        FilterUI fUI = new FilterUI(f);
        fUI.show();
    }
}
