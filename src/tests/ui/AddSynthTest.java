package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankTextfield;
import synth.ui.AdditiveUI;
import tests.ContextProvider;

import javax.swing.*;
import java.awt.*;

public class AddSynthTest {
    public static AudioContext ac = ContextProvider.ac();

    private static JFrame frame;
    //Layouts
    private static GridLayout presetGrid =  new GridLayout(1, 4);

    //panels
    private static BlankPanel mainPane;
    private static BlankPanel presetPane;
    private static BlankPanel synthPane;

    //buttons
    private static BlankButton presetDefault;
    private static BlankButton presetSaw;
    private static BlankButton presetSquare;
    private static BlankButton presetTriangle;

    //textfields and labels
    private static BlankTextfield basicFreq;
    private static BlankTextfield voices;
    private static JLabel basicFreqLabel;
    private static JLabel voicesLabel;

    private static AdditiveUI currUI;

    public static void main(String[] args) {
        frame = new JFrame("Additive Synthesis");
        mainPane = new BlankPanel();
        presetPane = new BlankPanel();
        currUI = new AdditiveUI(10, ac, AdditiveUI.DEFAULT, 220f);
        synthPane = currUI.contentPane;

        //setip layouts
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        //presetPane.setLayout(new BoxLayout(presetPane, BoxLayout.X_AXIS));
        presetPane.setLayout(presetGrid);

        //setup preset Buttons
        presetDefault = new BlankButton("Default");
        presetDefault.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(Integer.parseInt(voices.getText()), ac, AdditiveUI.DEFAULT, Float.parseFloat(basicFreq.getText()));
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
            frame.pack();
        });

        presetSaw = new BlankButton("Saw");
        presetSaw.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(Integer.parseInt(voices.getText()), ac, AdditiveUI.SAW, Float.parseFloat(basicFreq.getText()));
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
            frame.pack();
        });

        presetSquare = new BlankButton("Square");
        presetSquare.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(Integer.parseInt(voices.getText()), ac, AdditiveUI.SQUARE, Float.parseFloat(basicFreq.getText()));
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
            frame.pack();
        });

        presetTriangle = new BlankButton("Triangle");
        presetTriangle.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(Integer.parseInt(voices.getText()), ac, AdditiveUI.TRIANGLE, Float.parseFloat(basicFreq.getText()));
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
            frame.pack();
        });

        //setup textfield and Label
        basicFreq = new BlankTextfield("220.0");
        basicFreqLabel =  new JLabel("Basic Freq.:");
        voices = new BlankTextfield("10");
        voicesLabel = new JLabel("Voices:");

        //add buttons
        presetPane.add(presetDefault);
        presetPane.add(presetSaw);
        presetPane.add(presetSquare);
        presetPane.add(presetTriangle);
        presetPane.add(basicFreqLabel);
        presetPane.add(basicFreq);
        presetPane.add(voicesLabel);
        presetPane.add(voices);

        //add panels
        mainPane.add(presetPane);
        mainPane.add(synthPane);

        //start audio
        ac.start();

        frame.setContentPane(mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void removeSynthPane() {
        currUI.killAllOscillators();
        mainPane.remove(synthPane);
        mainPane.updateUI();
    }
}
