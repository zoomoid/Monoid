package synth.osc;

import net.beadsproject.beads.data.Buffer;

public interface WavetableOscillator {

    Buffer wave = null;

    Buffer getWave();

    WavetableOscillator setWave(Buffer wave);
}
