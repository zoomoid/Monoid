package tests.ui;

import synth.ui.components.swing.BlankToggle;
import javax.swing.*;

public class ToggleTest {

    JPanel pane;
    BlankToggle button;
    BlankToggle toggle;
    BlankToggle empty;
    public ToggleTest(){
        pane = new JPanel();
        button = new BlankToggle("Test");
        pane.add(button);
        toggle = new BlankToggle();
        pane.add(toggle);
        empty = new BlankToggle(true);
        pane.add(empty);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("ToggleTest");
        frame.setContentPane(new ToggleTest().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }

}