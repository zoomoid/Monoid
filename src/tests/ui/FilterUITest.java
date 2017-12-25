package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.osc.SmartOscillator;
import synth.ui.FilterUI;
import synth.ui.OscillatorUI;
import tests.ContextProvider;

public class FilterUITest {
    public static void main(String[] args){
        AudioContext ac = ContextProvider.ac();
        ac.start();

        SmartOscillator osc = new SmartOscillator(ac, 200f, Buffer.SAW, 5, 2f);
        osc.setName("Oscillator A");

        Filter f = new Filter(ac, BiquadFilter.Type.LPF, 300f, 1.414f, 0.71f);
        f.setName("Filter A");
        f.addInput(osc);

        ac.out.addInput(f);
        OscillatorUI oUI = new OscillatorUI(osc);
        oUI.show();

        FilterUI fUI = new FilterUI(f);
        fUI.show();
    }
}
