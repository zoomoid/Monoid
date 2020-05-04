package synth.filter.models;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.Static;

public class FilterFactory {

    public static MonoMoog createMonoMoog(AudioContext ac, FilterModel.Type type){
        return new MonoMoog(ac, type, 0f, 1f, 1f);
    }

    public static BiquadFilter createBiquadFilter(AudioContext ac, FilterModel.Type type){
        return new BiquadFilter(ac, type, 0f, 1f, 1f);
    }

}
