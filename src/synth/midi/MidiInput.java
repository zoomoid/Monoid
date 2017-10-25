package synth.midi;

public interface MidiInput {

    /**
     * trigger for MIDI events note toggle ON
     * @param midiKeyCode key code of the midi event
     * @param midiVelocity key velocity
     */
    void noteTriggerOn(int midiKeyCode, int midiVelocity);

    /**
     * trigger for MIDI events note toggle OFF
     * @param midiKeyCode key code
     * @param midiVelocity key velocity
     */
    void noteTriggerOff(int midiKeyCode, int midiVelocity);
}
