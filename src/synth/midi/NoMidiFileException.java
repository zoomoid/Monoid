package synth.midi;

class NoMidiFileException extends Exception {
    public NoMidiFileException() {
        super("File is no Midi File");
    }
}
