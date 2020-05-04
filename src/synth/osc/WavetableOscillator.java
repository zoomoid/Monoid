package synth.osc;

import net.beadsproject.beads.data.Buffer;
import synth.waves.TriangleBuffer;

public interface WavetableOscillator {

    enum WaveType {
        SINE, TRIANGLE, SAW, SQUARE, NOISE, CUSTOM
    }

    Waveform getWave();

    WavetableOscillator setWave(Waveform wave);

    String getWaveformName();

    WavetableOscillator setWaveform(Waveform waveform);
}
