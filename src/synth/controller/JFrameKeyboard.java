package synth.controller;

import javax.swing.*;
import java.awt.event.KeyEvent;
import synth.controller.Keys;

/**
 * Extended Version of JFrame, which is able to give frequencys of all notes in the fourth octave, controlled by the keyboard
 */
public class JFrameKeyboard extends JFrame{
    float frequency = 440f;
    boolean playing = false;

    public JFrameKeyboard() {
        this.addKeyListener(new Keys(this));
    }

    /**
     * Prints frequency for each middle note, whereas teh keyboardare the keys, starting from y as C4 to m as B(H)4
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

    /**
     *
     * @return current frequency
     */
    public float getFrequency() {
        this.validate();
        this.repaint();
        return frequency;
    }

    /**
     *
     * @param e the pressed Key, the method handles
     */
    public void handleKeyPressed(KeyEvent e) {
        setPlaying(true);
        int id = e.getID();
        char c = '0';
        if(id == KeyEvent.KEY_TYPED) {
            c = e.getKeyChar();
        }
        letterToFrequency(c);
    }

    public void handleKeyReleased(KeyEvent e) {
        setPlaying(false);
    }

    public void handleKeyTyped(KeyEvent e) {
        setPlaying(true);
        int id = e.getID();
        char c = '0';
        if(id == KeyEvent.KEY_TYPED) {
            c = e.getKeyChar();
        }
        letterToFrequency(c);
    }

    /**
     * Set frequency
     * @param newFreq new frequency
     */
    public void setFrequency(float newFreq) {
        frequency = newFreq;
    }

    /**
     * Changes status playing
     * @param p sets playing true or false
     */
    public void setPlaying(boolean p) {
        playing = p;
    }

    /**
     * Returns true, when the keys are playing and false else
     * @return
     */
    public boolean getPlaying() {
        return playing;
    }

}
