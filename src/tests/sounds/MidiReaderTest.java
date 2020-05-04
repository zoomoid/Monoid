package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import synth.controller.MidiOscController;
import synth.osc.BasicOscillator;
import synth.osc.Oscillator;
import synth.osc.Waveform;

import javax.swing.*;
import java.io.File;

public class MidiReaderTest {

    private static JFrame frame;

    private static JPanel contentPane;

    private static JFileChooser select;

    public static void main(String args[]) {
        AudioContext ac = new AudioContext();

        frame = new JFrame();
        contentPane = new JPanel();

        select = new JFileChooser();
        contentPane.add(select);

        File midiFile = select.getSelectedFile();

        BasicOscillator osc = new BasicOscillator(ac, 220f, Waveform.SQUARE);

        //MidiOscController controller = new MidiOscController(osc, bach_bourree.mid, ac);

        //controller.start();

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }
}
