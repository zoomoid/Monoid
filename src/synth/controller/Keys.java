package synth.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keys implements KeyListener{
    JFrameKeyboard test;

    public Keys(JFrameKeyboard test) {
        this.test = test;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        test.handleKeyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        test.handleKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        test.handleKeyReleased(e);
    }
}
