package tests.ui;

import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankToggleButton;

import javax.swing.*;

public class ButtonTest {

    JPanel pane;
    BlankButton button;
    BlankToggleButton toggle;
    public ButtonTest(){
        pane = new JPanel();
        button = new BlankButton("Button");
        pane.add(button);
        toggle = new BlankToggleButton("Toggle");
        pane.add(toggle);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("ButtonTest");
        frame.setContentPane(new ButtonTest().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }

}
