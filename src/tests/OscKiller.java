package tests;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Panner;
import net.beadsproject.beads.ugens.RangeLimiter;
import org.jaudiolibs.beads.AudioServerIO;
import synth.osc.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OscKiller {
    private JPanel container;
    private JButton start;
    private JButton kill;
    private JButton removeVoices;
    private JButton addVoices;
    private JLabel voicesNumber;
    private JLabel unisonSpread;

    public static AudioContext ac = new AudioContext(new AudioServerIO.JavaSound("Primärer Soundtreiber"), 4096, new IOAudioFormat(44100, 24, 0, 2));

    public SmartOscillator osc;

    public RangeLimiter limiter;

    public OscKiller(){
        ac.start();
        osc = new SmartOscillator(ac, 220f, Buffer.SAW, 1, 2f, 1f);
        limiter = new RangeLimiter(ac, 2);
        Panner panner = new Panner(ac, 0f);
        panner.addInput(osc.output());
        limiter.addInput(panner);
        osc.setup();
        osc.pause();
        ac.out.addInput(limiter);
        unisonSpread.setText("Unison Pitch Spread: " + osc.getSpread() + " Hz");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                osc.start();
            }
        });
        kill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                osc.pause();
            }
        });
        removeVoices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                osc.pause();
                if(osc.isPaused()){
                    osc.setVoices(Math.max(1, osc.getVoices() - 1));
                }
                voicesNumber.setText("Currently oscillating with " + osc.getVoices() + " voice(s)");
                osc.start();
            }
        });
        addVoices.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                osc.pause();
                if(osc.isPaused()){
                    osc.setVoices(Math.min(16, osc.getVoices() + 1));
                }
                voicesNumber.setText("Currently oscillating with " + osc.getVoices() + " voice(s)");
                osc.start();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("OscKiller");
        frame.setContentPane(new OscKiller().container);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
