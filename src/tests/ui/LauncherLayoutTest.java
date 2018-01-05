package tests.ui;

import synth.ui.components.swing.BlankToggleButton;

import javax.swing.*;

public class LauncherLayoutTest {
    /**The frame*/
    private static JFrame frame;

    /**All the used Panes*/
    private static JPanel mainPane;
    private static JPanel tabPane;

    /**Toggle buttons for tabs*/
    private static BlankToggleButton oscButton;
    private static BlankToggleButton addSynthButton;
    private static BlankToggleButton oscAndFilter;
    private static BlankToggleButton amSynth;
    private static BlankToggleButton fmSynth;

    private static BlankToggleButton currSelected;

    public static void main(String args[]) {
        frame = new JFrame("Monoid");
        mainPane = new JPanel();

        //setup tab pane
        tabPane = new JPanel();
        //and buttons for tab pane
        oscButton = new BlankToggleButton("Oscillator");
        addSynthButton = new BlankToggleButton("Additive Section");
        oscAndFilter = new BlankToggleButton("Osc. and Filter");
        amSynth = new BlankToggleButton("AM");
        fmSynth = new BlankToggleButton("FM");

        //start with standard synthesizer
        currSelected = oscButton;
        oscButton.toggle();

        //add action listeners to buttons
        oscButton.addActionListener(e -> {
            changeButton(oscButton);
        });

        addSynthButton.addActionListener(e -> {
            changeButton(addSynthButton);
        });

        oscAndFilter.addActionListener(e -> {
            changeButton(oscAndFilter);
        });

        amSynth.addActionListener(e -> {
            changeButton(amSynth);
        });

        fmSynth.addActionListener(e -> {
            changeButton(fmSynth);
        });

        //set Layout of tab pane
        tabPane.add(oscButton);
        tabPane.add(addSynthButton);
        tabPane.add(oscAndFilter);
        tabPane.add(amSynth);
        tabPane.add(fmSynth);
        tabPane.setLayout(new BoxLayout(tabPane, BoxLayout.X_AXIS));

        //add panes to main pane
        mainPane.add(tabPane);

        //setup layout
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));

        frame.setContentPane(mainPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void changeButton(BlankToggleButton button) {
        if(!(currSelected == button)) {
            currSelected.toggle();
            currSelected = button;
        }
    }
}
