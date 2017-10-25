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

public class KeyboardTest {

    private JPanel container = new JPanel();

    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

    float frequency = 440f;

    public static SmartOscillator osc;

    public RangeLimiter limiter;

    static Keys keys = new Keys(osc);


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
        osc.setup();
        ac.out.addInput(g);

    }

    /**
     * Method for setting up actions for pressed Keys in the Keys-Section from the Keyboard
     * (Keys = Klaviatur (ger), Keyboard = Tastatur (ger))
     * @param panel JPanel of this class
     */
    public static void setupPanelMaps(JPanel panel) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "a");
        panel.getActionMap("a", keys.midiMessage('a'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "w");
        panel.getActionMap("w", keys.midiMessage('w'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "s");
        panel.getActionMap("s", keys.midiMessage('s'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "e");
        panel.getActionMap("e", keys.midiMessage('e'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "d");
        panel.getActionMap("d", keys.midiMessage('d'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "f");
        panel.getActionMap("f", keys.midiMessage('f'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("T"), "t");
        panel.getActionMap("t", keys.midiMessage('t'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("G"), "g");
        panel.getActionMap("g", keys.midiMessage('g'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), "z");
        panel.getActionMap("z", keys.midiMessage('z'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "h");
        panel.getActionMap("h", keys.midiMessage('h'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("U"), "u");
        panel.getActionMap("u", keys.midiMessage('u'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("J"), "j");
        panel.getActionMap("j", keys.midiMessage('j'));
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("K"), "k");
        panel.getActionMap("k", keys.midiMessage('k'));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("KeyboardTest");
        setupPanelMaps(container);
        frame.setContentPane(new KeyboardTest().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
