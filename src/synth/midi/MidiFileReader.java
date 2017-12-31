package synth.midi;

import synth.osc.Oscillator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MidiFileReader {
    /**The Midi File */
    File midiFile;

    /**The controlled Oscillator */
    Oscillator controlledOsc;

    /**The FileInputStream to read bytes and convert them to hex */
    FileInputStream reader;

    /**Basic Constructor */
    public MidiFileReader(File midiFile, Oscillator osc) {
        this.midiFile = midiFile;
        this.controlledOsc = osc;
        try {
            reader = new FileInputStream(this.midiFile);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found");
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

    //TODO adjust variables (not declared yet) with the information gained from the midi header
    public void analyzeHeader() {

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
            case 'f': return 15;
            case 'e': return 14;
            case 'd': return 13;
            case 'c': return 12;
            case 'b': return 11;
            case 'a': return 10;
            case '9': case'8': case'7': case '6': case '5': case '4': case '3': case '2': case '1': case '0': return Integer.parseInt(Character.toString(hex));
            default: break;
        }
        return result;
    }

}
