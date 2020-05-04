package synth.osc;

import net.beadsproject.beads.data.Buffer;
import synth.waves.TriangleBuffer;

public class Waveform {

    public Buffer getBuffer() {
        return waveformBuffer;
    }

    protected Buffer waveformBuffer;

    public String getName() {
        return name;
    }

    protected final String name;

    public static final Waveform SINE = new Waveform("SINE");
    public static final Waveform TRIANGLE = new Waveform("TRIANGLE");
    public static final Waveform SAW = new Waveform("SAW");
    public static final Waveform SQUARE = new Waveform("SQUARE");
    public static final Waveform NOISE = new Waveform("NOISE");

    public Waveform(Buffer b, String name) {
        this.name = name;
        this.waveformBuffer = b;
    }

    public Waveform(String name){
        this.name = name;
        switch (this.name) {
            case "TRIANGLE":
                this.waveformBuffer = TriangleBuffer.TRIANGLE;
                break;
            case "SAW":
                this.waveformBuffer = Buffer.SAW;
                break;
            case "SQUARE":
                this.waveformBuffer = Buffer.SQUARE;
                break;
            case "SINE":
                this.waveformBuffer = Buffer.SINE;
                break;
            case "NOISE":
                this.waveformBuffer = Buffer.NOISE;
                break;
            default:
                this.waveformBuffer = null;
                break;
        }
    }
}
