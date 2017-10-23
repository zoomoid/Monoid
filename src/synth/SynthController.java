package synth;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import org.jaudiolibs.beads.AudioServerIO;
import synth.filter.*;
import synth.osc.*;
import synth.ui.*;
import synth.effect.*;

/**
 * Outermost controller wrapper for modular parts
 */
public class SynthController {
    private final UIController ui;
    private final OscillatorController oscs;
    private final FilterController filters;
    private final EffectController effects;

    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

    /**
     * Initializes all controllers of the synthesizer
     */
    public SynthController(){
        oscs = new OscillatorController(this);
        filters = new FilterController(this);
        effects = new EffectController(this);
        ui = new UIController(this);
    }

    public UIController getUi() {
        return ui;
    }

    public OscillatorController getOscs() {
        return oscs;
    }

    public FilterController getFilters() {
        return filters;
    }

    public EffectController getEffects() {
        return effects;
    }
}
