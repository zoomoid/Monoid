package synth.filter.models;

import net.beadsproject.beads.core.AudioContext;
import synth.modulation.Static;


public class FilterFactory {

    public static MonoMoog createMonoMoog(AudioContext ac, FilterModel.Mode mode){
        return new MonoMoog(ac, 2, mode, new Static(ac, 1f), new Static(ac, 1f), new Static(ac, 1f));
    }

    public static BiquadFilter createBiquadFilter(AudioContext ac, FilterModel.Mode mode){
        BiquadFilter f = (BiquadFilter)(new BiquadFilter(ac, 2).setFrequency(0f).setQ(0f));
        return switch (mode) {
            case LPF -> f.setType(FilterModel.Mode.LPF);
            case HPF -> f.setType(FilterModel.Mode.HPF);
            default -> f.setType(FilterModel.Mode.LPF);
        };
    }

}
