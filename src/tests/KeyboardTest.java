package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Panner;
import net.beadsproject.beads.ugens.RangeLimiter;
import org.jaudiolibs.beads.AudioServerIO;
import synth.controller.JFrameKeyboard;
import synth.osc.SmartOscillator;

import javax.swing.*;

public class KeyboardTest {

    private JPanel container = new JPanel();

    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

    float frequency = 440f;

    public static SmartOscillator osc;

    public RangeLimiter limiter;

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

    public static void main(String[] args) {
        JFrameKeyboard frame = new JFrameKeyboard();
        frame.setContentPane(new KeyboardTest().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        while(true) {
            osc.setFrequency(frame.getFrequency());
            System.out.println("Playing " + frame.getFrequency() + " Hz");
        }
    }
}
