package synth.auxilliary;

public class MIDIUtils {

    /**
     * Converts a Midi Pitch Value (int) to its corresponding frequency in Hz
     * taken from http://newt.phys.unsw.edu.au/jw/notes.html
     * @param midiKey MIDI protocol key value
     * @return frequency in Hz as float
     */
    public static float midi2frequency(int midiKey){
        // half tone steps increment in multiples of the twelth root in frequency
        // calculate the offset of the midiKey from a known midiKey = frequency (A4, in this case)
        double exponent = (midiKey - 69.0) / 12.0;
        // since octaves stack in powers of two, calculate 2^(offset/12) * 440Hz from root A4
        return (float)(Math.pow(2, exponent) * 440.0f);
    }
}