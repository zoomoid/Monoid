package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Panner;
import net.beadsproject.beads.ugens.RangeLimiter;
import org.jaudiolibs.beads.AudioServerIO;
import synth.osc.SmartOscillator;
import synth.controller.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KeyboardTest {

    private static JPanel container;

    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

    float frequency = 440f;

    public static SmartOscillator osc;

    public RangeLimiter limiter;

    static Keys keys;


    /**
     * Constructor for setting up simple UI to control a SmartOscillator
     */
    public KeyboardTest() {
        ac.start();
        osc = new SmartOscillator(ac, 220f, Buffer.SAW, 1, 2f, 1f);
        limiter = new RangeLimiter(ac, 2);
        Panner panner = new Panner(ac, 0f);
        panner.addInput(osc.output());
        limiter.addInput(panner);
        Gain g = new Gain(ac, 1, 0.1f);
        g.addInput(limiter);
        ac.out.addInput(g);

    }

    /**
     * Method for setting up actions for pressed Keys in the Keys-Section from the Keyboard
     * (Keys = Klaviatur (ger), Keyboard = Tastatur (ger))
     * @param panel JPanel of this class
     */
    public static void setupPanelMaps(JPanel panel) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "a");
        panel.getActionMap().put("a", new KeyAction('a', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "w");
        panel.getActionMap().put("w", new KeyAction('w', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "s");
        panel.getActionMap().put("s", new KeyAction('s', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "e");
        panel.getActionMap().put("e", new KeyAction('e', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "d");
        panel.getActionMap().put("d", new KeyAction('d', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "f");
        panel.getActionMap().put("f", new KeyAction('f', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("T"), "t");
        panel.getActionMap().put("t", new KeyAction('t', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("G"), "g");
        panel.getActionMap().put("g", new KeyAction('g', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), "z");
        panel.getActionMap().put("z", new KeyAction('z', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "h");
        panel.getActionMap().put("h", new KeyAction('h', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("U"), "u");
        panel.getActionMap().put("u", new KeyAction('u', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("J"), "j");
        panel.getActionMap().put("j", new KeyAction('j', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("K"), "k");
        panel.getActionMap().put("k", new KeyAction('k', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Y"), "y");
        panel.getActionMap().put("y", new KeyAction('y', keys));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("X"), "x");
        panel.getActionMap().put("x", new KeyAction('x', keys));
    }

    public static void main(String[] args) {
        container = new JPanel();
        JFrame frame = new JFrame("KeyboardTest");
        frame.setContentPane(new KeyboardTest().container);
        keys = new Keys(osc);
        setupPanelMaps(container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
