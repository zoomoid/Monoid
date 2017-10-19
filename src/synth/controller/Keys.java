package synth.controller;

import net.beadsproject.beads.core.AudioContext;
import synth.osc.Oscillator;
import synth.osc.SmartOscillator;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keys implements KeyListener{
    Oscillator oscillator;
    float frequency;

    //TODO add constructor controlling OscialltorManager

    /**
     * Constructor for testing purpouses
     */
    public Keys() {
        AudioContext ac = new AudioContext();
        this.oscillator = new SmartOscillator(ac, 440f);
    }

    /**
     * Basic constructor, the given oscillator keeps playing its initial frequency
     * @param osc Oscillaotr, which is controlled by keys
     */
    public Keys(Oscillator osc) {
        this.oscillator = osc;
        this.frequency = osc.getFrequency();
    }

    /**
     * Returns Frequency for each middle note, whereas teh keyboardare the keys, starting from y as C4 to m as B(H)4
     * @param letter the played Key
     */
    public void letterToFrequency(char letter) {
        switch(letter) {
            case 'y': frequency = 261.63f; System.out.println("Playing C4"); break;
            case 's': frequency = 277.18f; System.out.println("Playing C#4/Db4"); break;
            case 'x': frequency = 293.66f; System.out.println("Playing D4"); break;
            case 'd': frequency = 311.13f; System.out.println("Playing D#4/Eb4"); break;
            case 'c': frequency = 329.63f; System.out.println("Playing E4"); break;
            case 'v': frequency = 349.23f; System.out.println("Playing F4"); break;
            case 'g': frequency = 369.99f; System.out.println("Playing F#4/Gb4"); break;
            case 'b': frequency = 392.00f; System.out.println("Playing G4"); break;
            case 'h': frequency = 415.30f; System.out.println("Playing G#4/Ab4"); break;
            case 'n': frequency = 440.00f; System.out.println("Playing A4"); break;
            case 'j': frequency = 466.16f; System.out.println("Playing A#4/Bb4"); break;
            case 'm': frequency = 493.88f; System.out.println("Playing B4"); break;
            default: break;
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
        int id = e.getID();
        char c = '0';
        if(id == KeyEvent.KEY_TYPED) {
            c = e.getKeyChar();
        }
        letterToFrequency(c);
        System.out.println(frequency);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int id = e.getID();
        char c = '0';
        if(id == KeyEvent.KEY_PRESSED) {
            c = e.getKeyChar();
        }
        letterToFrequency(c);
        System.out.println(frequency);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int id =  e.getID();
        char c = '0';
        if(id == KeyEvent.KEY_RELEASED) {
            c = e.getKeyChar();
        }
        letterToFrequency(c);
        System.out.println(frequency);
    }
}
