package synth.osc;

import net.beadsproject.beads.data.Buffer;

public interface WavetableOscillator {

    enum WaveType {
        SINE, TRIANGLE, SAW, SQUARE, NOISE, CUSTOM
    }

    Buffer getWave();

    WavetableOscillator setWave(Buffer wave);

    WaveType getWaveType();

    WavetableOscillator setWaveType(WaveType waveType);

    default void determineWaveType(Buffer wave){
        if(wave == Buffer.SINE){
            this.setWaveType(WaveType.SINE);
        } else if(wave == Buffer.TRIANGLE){
            this.setWaveType(WaveType.TRIANGLE);
        } else if(wave == Buffer.SAW) {
            this.setWaveType(WaveType.SAW);
        } else if(wave == Buffer.SQUARE){
            this.setWaveType(WaveType.SQUARE);
        } else if(wave == Buffer.NOISE){
            this.setWaveType(WaveType.NOISE);
        } else {
            this.setWaveType(WaveType.CUSTOM);
        }
    }
}
