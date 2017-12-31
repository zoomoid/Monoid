package synth.midi;

import synth.osc.Oscillator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MidiFileReader{
    /**The Midi File, can be NULL if there is an error */
    public File midiFile;

    /**The controlled Oscillator */
    Oscillator controlledOsc;

    /**The FileInputStream to read bytes and convert them to hex */
    FileInputStream reader;

    /** Necessary variables containing basic information about the midi file */
    int tracks; //number of tracks
    int fileFormat; //single track, synchronous tracks, asynchronous tracks (see analyze header)
    int deltaTicks; //TODO add reference

    /**Basic Constructor */
    public MidiFileReader(File midiFile, Oscillator osc) {
        this.midiFile = midiFile;
        this.controlledOsc = osc;
        try {
            reader = new FileInputStream(this.midiFile);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found");
        }
        try {
            analyzeHeader();
        } catch (NoMidiFileException nomid) {
            this.midiFile = null;
        }
    }

    /**
     * converts a String holding an hexadecimal number into an int
     * @param hex String, holding hexadecimal number
     * @return hexadecimal number as int
     */
    public int hexToInt(String hex) {
        int result = 0;
        for(int i = 0; i < hex.length(); i++) {
            char currChar = hex.substring(i, i).toCharArray()[0];
            //result is successed by the current value powered by the current digit in the number
            result += Math.pow(hexCharToInt(currChar), Math.pow(16, hex.length() - i - 1));
        }
        return result;
    }

    /**
     * Checks whether the midiFile header is correct or not
     * @throws NoMidiFileException if the midiFile header is not a header of a midiFile
     */
    public void analyzeHeader()  throws NoMidiFileException{
        //check whether midi header is correct or not
        byte[] header = new byte[14];
        /*
        14 is header length
        if header length is smaller than 14, the file cant be a midi file
        First four bytes are 4d 54 68 64 (ASCII MThd)
        Following four bytes are 00 00 00 06 (6, length of actual midi header)
        Last six bytes are ff ff nn nn dd dd where
        ff ff is the file format
            0 - single track
            1 - multiple, synchronous tracks
            2 - multiple tracks, asynchronous
         nn nn is the number of tracks in the midi file
         dd dd is the number of delta ticks per quarter note
         */
        try {
            if (this.reader.read(header, 0, 14) == 14) {
                int headerValue = header[0] * 1000 + header[1] * 100 + header[2] * 10 + header[3];
                //check if header is correct
                if(headerValue != hexToInt("4d546864")) {
                    System.out.println("Wrong Midi header");
                    throw new NoMidiFileException();
                } else if(header[7] != 6) { //check if length parameter in the header ie correct
                    System.out.println("Wrong length parameter in midi header");
                    throw new NoMidiFileException();
                }
                //setup values
                this.fileFormat = header[8] * 10 + header[9];
                this.tracks = header[10] * 10 + header[11];
                this.deltaTicks = header[12] * 10 + header[13];
            } else {
                System.out.println("Wrong header length");
                throw new NoMidiFileException();
            }
        } catch(IOException e) {
            throw new NoMidiFileException();
        }
    }

    //TODO analyzes all track chunks and buffers them into lists. allows synchronous use of tracks
    public void analyzeChunks() {

    }

    /**
     * Converts a single char as a hexadecimal number into an int
     * @param hex char as hexadecimal number
     * @return hex as int
     */
    public int hexCharToInt(char hex) {
        int result = -1;
        //convert hex char to int
        switch(hex) {
            case 'f': case 'F': return 15;
            case 'e': case 'E': return 14;
            case 'd': case 'D': return 13;
            case 'c': case 'C': return 12;
            case 'b': case 'B': return 11;
            case 'a': case 'A': return 10;
            case '9': case'8': case'7': case '6': case '5': case '4': case '3': case '2': case '1': case '0': return Integer.parseInt(Character.toString(hex));
            default: break;
        }
        return result;
    }

    /**
     * Checks whether midiFile is correct
     * @return true, if midiFile is correct, false if its not. midiFile is null then
     */
    public boolean isMidiFileCorrect() {
        if(midiFile == null) {
            return false;
        } else {
            return true;
        }
    }
}
