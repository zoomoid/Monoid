package synth.controller;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeyAction extends AbstractAction {
    //The key, which fired the KeyAction Event
    char key;

    //The Keys, on which the key, firing the event, is on
    Keys keys;

    /**
     * Constructor, creating the action
     * @param key The key, which fired the KeyAction Event
     * @param keys The Keys, on which the key, firing the event, is on
     */
    public KeyAction(char key, Keys keys) {
        super();
        this.key = key;
        this.keys = keys;
    }

    /**
     * Calling the midiMessage method in keys
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        keys.midiMessage(key);
    }
}
