package synth.ui.providers;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.Add;
import synth.auxilliary.ContextProvider;
import synth.ui.AdditiveUI;
import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankLabel;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankTextfield;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AdditiveUIProvider implements Provider {
    private AudioContext ac;
    //Layouts
    private GridLayout presetGrid;

    //panels
    private BlankPanel mainPane;
    private BlankPanel presetPane;
    private BlankPanel synthPane;

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

    private AdditiveUI currUI;
    private JFrame ui;

    public AdditiveUIProvider() {
        this.ac = ContextProvider.ac();
        this.ac.start();
        this.mainPane = new BlankPanel();
        this.presetPane = new BlankPanel();
        this.currUI = new AdditiveUI(10, ac, AdditiveUI.DEFAULT);
        this.synthPane = currUI.contentPane;
        this.presetGrid = new GridLayout(1, 4);
        // setup layouts
        this.mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        // presetPane.setLayout(new BoxLayout(presetPane, BoxLayout.X_AXIS));
        this.presetPane.setLayout(presetGrid);

        // setup preset Buttons
        this.presetDefault = new BlankButton("Default");
        this.presetDefault.addActionListener(e -> {
            this.spawnUI(AdditiveUI.DEFAULT);
        });

        this.presetSaw = new BlankButton("Saw");
        this.presetSaw.addActionListener(e -> {
            this.spawnUI(AdditiveUI.SAW);
        });

        this.presetSquare = new BlankButton("Square");
        this.presetSquare.addActionListener(e -> {
            this.spawnUI(AdditiveUI.SQUARE);
        });

        this.presetTriangle = new BlankButton("Triangle");
        this.presetTriangle.addActionListener(e -> {
            this.spawnUI(AdditiveUI.TRIANGLE);
        });

        //setup textfield and Label
        this.basicFreq = new BlankTextfield("220.0");
        this.basicFreqLabel =  new BlankLabel("Basic Freq.:");
        this.voices = new BlankTextfield("10");
        this.voicesLabel = new BlankLabel("Voices:");

        //add buttons
        this.presetPane.add(presetDefault);
        this.presetPane.add(presetSaw);
        this.presetPane.add(presetSquare);
        this.presetPane.add(presetTriangle);
        this.presetPane.add(basicFreqLabel);
        this.presetPane.add(basicFreq);
        this.presetPane.add(voicesLabel);
        this.presetPane.add(voices);

        //add panels
        this.mainPane.add(presetPane);
        this.mainPane.add(synthPane);

        this.ui = new JFrame("Additiv");
        this.ui.setContentPane(this.mainPane);
        this.ui.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.ui.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                ac.stop();
                ui.setVisible(false);
                ui.dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        this.ui.pack();
        this.ui.setResizable(true);
        this.ui.setVisible(true);
    }

    private void removeSynthPane() {
        this.currUI.killAllOscillators();
        this.mainPane.remove(synthPane);
        this.mainPane.updateUI();
    }

    private void spawnUI(AdditiveUI.Preset preset){
        this.removeSynthPane();
        this.currUI = new AdditiveUI(Integer.parseInt(voices.getText()), ac, preset, Float.parseFloat(basicFreq.getText()));
        this.synthPane = currUI.contentPane;
        this.mainPane.add(synthPane);
        this.mainPane.updateUI();
        this.ui.pack();
    }

    public void close(){
        this.ui.dispatchEvent(new WindowEvent(this.ui, WindowEvent.WINDOW_CLOSING));
    }
}
