package synth.controller;

import synth.osc.Oscillator;

import javax.sound.midi.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.LinkedList;

public class Keys{
    /**
     * The controlled Oscillator(later: OscillatorManager)
     */
    Oscillator osc;

    /**
     * current ocatave, interval: [-1,9]
     */
    int octave = 4;



    /**
     * Array of chars, in which the Index is a midi code: (octave+1)*12+i,  charToIndex[i] = key
     */
    protected char[] charToIndex = new char[12];
    /**
     * Setting up the oscillator
     * @param osc given oscillator
     */
    public Keys(Oscillator osc) {
        this.osc = osc;
        fillCharArray();
    }

    LinkedList<Integer> active = new LinkedList<Integer>();

    /**
     * transforming the key on the keyboard into a key of a piano and creating a ShortMessage(MIDI)
     * @param key the current pressed Key
     */
    public void midiMessage(char key) {
            switch (key) {
                case 'a':
                    osc.send(createShortMessage('a'), -1);
                    break;
                case 'w':
                    osc.send(createShortMessage('w'), -1);
                    break;
                case 's':
                    osc.send(createShortMessage('s'), -1);
                    break;
                case 'e':
                    osc.send(createShortMessage('e'), -1);
                    break;
                case 'd':
                    osc.send(createShortMessage('d'), -1);
                    break;
                case 'f':
                    osc.send(createShortMessage('f'), -1);
                    break;
                case 't':
                    osc.send(createShortMessage('t'), -1);
                    break;
                case 'g':
                    osc.send(createShortMessage('g'), -1);
                    break;
                case 'z':
                    osc.send(createShortMessage('z'), -1);
                    break;
                case 'h':
                    osc.send(createShortMessage('h'), -1);
                    break;
                case 'u':
                    osc.send(createShortMessage('u'), -1);
                    break;
                case 'j':
                    osc.send(createShortMessage('j'), -1);
                    break;
                case 'k':
                    octave++;
                    ShortMessage shortMessage = createShortMessage('a');
                    osc.send(shortMessage, -1);
                    octave--;
                    break;
                case 'y':
                    this.octave--;
                    System.out.println("Derzeitige Oktave: " + octave);
                    break;
                case 'x':
                    this.octave++;
                    System.out.println("Derzeitige Oktave: " + octave);
                    break;
                default:
                    break;
            }
    }

    /**
     * creates a ShortMessage for the played in Key in the current octave
     * if note is on, it turns note off
     * @param key the played key
     * @return ShortMessage(MIDI), default: middle a
     */
    public ShortMessage createShortMessage(char key) {
        //calculates midi value from given key, accounts current octave
        int midiCode = charToMidiNumber(key) + (octave+1) * 12;
        if(octave >= -1 && octave <= 9) {
            try {
                if(!active.contains(midiCode)) {
                    active.clear();
                    active.add(midiCode);
                    System.out.println(octave);
                    return new ShortMessage(ShortMessage.NOTE_ON, midiCode, 88);
                } else {
                    active.remove((Integer) midiCode);
                    System.out.println("Note off" + octave);
                    return new ShortMessage(ShortMessage.NOTE_OFF, midiCode, 88);
                }
            } catch (InvalidMidiDataException e) {
                System.out.println("Invalid Midi Data");
                return null;
            }
        }
        return null;
    }

    /**
     * turns the current char into a MIDI Number
     * @param key current played key
     * @return MIDI signal which equals the current played char
     */
    public int charToMidiNumber(char key) {
        for(int i = 0; i < charToIndex.length; i++) {
            if(charToIndex[i] == key) {
                if(octave == 9 && i > 7) {
                    return 7;
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * fills the array charToIndex
     */
    public void fillCharArray() {
        charToIndex[0] = 'a';
        charToIndex[1] = 'w';
        charToIndex[2] = 's';
        charToIndex[3] = 'e';
        charToIndex[4] = 'd';
        charToIndex[5] = 'f';
        charToIndex[6] = 't';
        charToIndex[7] = 'g';
        charToIndex[8] = 'z';
        charToIndex[9] = 'h';
        charToIndex[10] = 'u';
        charToIndex[11] = 'j';
    }
}
