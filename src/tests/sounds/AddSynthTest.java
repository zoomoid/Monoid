package tests.sounds;

import net.beadsproject.beads.core.AudioContext;
import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankPanel;
import synth.ui.experimental.AdditiveUI;
import tests.ContextProvider;

import javax.swing.*;
import java.awt.*;

public class AddSynthTest {
    public static AudioContext ac = ContextProvider.ac();

    private static GridLayout mainGrid =  new GridLayout(2, 1);
    private static GridLayout presetGrid = new GridLayout(1, 4, 5, 5);

    private static BlankPanel mainPane;
    private static BlankPanel presetPane;
    private static BlankPanel synthPane;
    private static BlankButton presetDefault;
    private static BlankButton presetSaw;
    private static BlankButton presetSquare;
    private static BlankButton presetTriangle;

    private static AdditiveUI currUI;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Additive Synthesis");
        mainPane = new BlankPanel();
        presetPane = new BlankPanel();
        currUI = new AdditiveUI(5, ac, AdditiveUI.DEFAULT);
        synthPane = currUI.contentPane;

        //setip layouts
        mainPane.setLayout(mainGrid);
        presetPane.setLayout(presetGrid);

        //setup preset Buttons
        presetDefault = new BlankButton("Default");
        presetDefault.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(5, ac, AdditiveUI.DEFAULT);
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
        });

        presetSaw = new BlankButton("Saw");
        presetSaw.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(15, ac, AdditiveUI.SAW);
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
        });

        presetSquare = new BlankButton("Square");
        presetSquare.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(10, ac, AdditiveUI.SQUARE);
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
        });

        presetTriangle = new BlankButton("Triangle");
        presetTriangle.addActionListener(e -> {
            removeSynthPane();
            currUI = new AdditiveUI(10, ac, AdditiveUI.TRIANGLE);
            synthPane = currUI.contentPane;
            mainPane.add(synthPane);
            mainPane.updateUI();
        });

        //add buttons
        presetPane.add(presetDefault);
        presetPane.add(presetSaw);
        presetPane.add(presetSquare);
        presetPane.add(presetTriangle);
        mainPane.add(presetPane);
        mainPane.add(synthPane);
        ac.start();

        frame.setContentPane(mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void removeSynthPane() {
        currUI.killAllOscillators();
        mainPane.remove(synthPane);
        mainPane.updateUI();
    }
}
