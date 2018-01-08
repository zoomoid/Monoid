package synth.ui;

import net.beadsproject.beads.core.AudioContext;
import synth.auxilliary.ContextProvider;
import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankLabel;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankTextfield;

import javax.swing.*;
import java.awt.*;

public class AdditiveUIProvider {
    public AudioContext ac;

    //Layouts
    private GridLayout presetGrid =  new GridLayout(1, 4);

    //panels
    public BlankPanel mainPane;
    public BlankPanel presetPane;
    public BlankPanel synthPane;

    //buttons
    private BlankButton presetDefault;
    private BlankButton presetSaw;
    private BlankButton presetSquare;
    private BlankButton presetTriangle;

    //textfields and labels
    private BlankTextfield basicFreq;
    private BlankTextfield voices;
    private BlankLabel basicFreqLabel;
    private BlankLabel voicesLabel;

    public AdditiveUI currUI;

    public AdditiveUIProvider(AudioContext ac, JFrame frame) {
        this.ac = ac;
        mainPane = new BlankPanel();
        presetPane = new BlankPanel();
        currUI = new AdditiveUI(10, ac, AdditiveUI.DEFAULT);
        synthPane = currUI.contentPane;

        // setup layouts
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        // presetPane.setLayout(new BoxLayout(presetPane, BoxLayout.X_AXIS));
        presetPane.setLayout(presetGrid);

        // setup preset Buttons
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
        basicFreqLabel =  new BlankLabel("Basic Freq.:");
        voices = new BlankTextfield("10");
        voicesLabel = new BlankLabel("Voices:");

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
    }

    public void removeSynthPane() {
        currUI.killAllOscillators();
        mainPane.remove(synthPane);
        mainPane.updateUI();
    }
}
