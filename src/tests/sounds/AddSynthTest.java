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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Additive Synthesis");
        mainPane = new BlankPanel();
        presetPane = new BlankPanel();
        synthPane = new AdditiveUI(5, ac, AdditiveUI.DEFAULT).contentPane;

        //setip layouts
        mainPane.setLayout(mainGrid);
        presetPane.setLayout(presetGrid);

        //setup preset Buttons
        presetDefault = new BlankButton("Default");
        presetDefault.addActionListener(e -> {
            mainPane.remove(synthPane);
            synthPane = new AdditiveUI(5, ac, AdditiveUI.DEFAULT).contentPane;
            mainPane.add(synthPane);
        });

        presetSaw = new BlankButton("Saw");
        presetSaw.addActionListener(e -> {
            mainPane.remove(synthPane);
            synthPane = new AdditiveUI(5, ac, AdditiveUI.SAW).contentPane;
            mainPane.add(synthPane);
        });

        presetSquare = new BlankButton("Square");
        presetSquare.addActionListener(e -> {
            mainPane.remove(synthPane);
            synthPane = new AdditiveUI(5, ac, AdditiveUI.SQUARE).contentPane;
            mainPane.add(synthPane);
        });

        presetTriangle = new BlankButton("Triangle");
        presetTriangle.addActionListener(e -> {
            mainPane.remove(synthPane);
            synthPane = new AdditiveUI(5, ac, AdditiveUI.TRIANGLE).contentPane;
            mainPane.add(synthPane);
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
}
