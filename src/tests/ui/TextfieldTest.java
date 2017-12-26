package tests.ui;

import synth.ui.components.swing.BlankTextfield;

import javax.swing.*;

public class TextfieldTest {

    JPanel pane;
    BlankTextfield field;

    public TextfieldTest(){
        pane = new JPanel();
        field = new BlankTextfield("Test");
        pane.add(field);
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("Textfield");
        frame.setContentPane(new TextfieldTest().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }
}
