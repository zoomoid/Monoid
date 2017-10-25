package synth.controller;

import synth.osc.Oscillator;

import javax.sound.midi.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class Keys{
    Oscillator osc;

    public Keys(Oscillator osc) {
        this.osc = osc;
    }

    public void midiMessage(char key) {
        ShortMessage shortMessage;
        try {
            switch (key) {
                case 'a':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 60, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'w':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 61, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 's':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 62, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'e':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 63, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'd':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 64, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'f':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 65, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 't':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 66, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'g':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 67, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'z':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 68, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'h':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 69, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'u':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 70, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'j':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 71, 88);
                    osc.send(shortMessage, -1);
                    break;
                case 'k':
                    shortMessage = new ShortMessage(ShortMessage.NOTE_ON, 72, 88);
                    osc.send(shortMessage, -1);
                    break;

                default:
                    break;
            }
        } catch (InvalidMidiDataException inv) {
            System.out.println("Invalid Midi Data");
        }
    }
}
