package tests.ui;

import synth.ui.components.swing.BlankImageSpinner;
import synth.ui.components.swing.BlankSpinner;

import javax.swing.*;

public class SpinnerTest {

    JPanel pane;
    BlankImageSpinner spinner;
    BlankSpinner blankspinner;
    public SpinnerTest(){
        pane = new JPanel();
        ImageIcon[] icons = {
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\sine.png", "sine"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\triangle.png", "triangle"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\saw.png", "saw"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\square.png", "square"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\noise.png", "noise")
        };
        spinner = new BlankImageSpinner(new SpinnerListModel(icons));
        pane.add(spinner);
        blankspinner = new BlankSpinner(new SpinnerNumberModel());
        pane.add(blankspinner);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("SpinnerTest");
        frame.setContentPane(new SpinnerTest().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }

}

