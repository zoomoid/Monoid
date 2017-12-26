package tests.ui;

import synth.ui.components.swing.BlankTextfield;

import javax.swing.*;

public class TextfieldTest {

    JPanel pane;
    BlankTextfield field, field2;

    public TextfieldTest(){
        pane = new JPanel();
        field = new BlankTextfield("Test");
        pane.add(field);
        field2 = new BlankTextfield();
        pane.add(field2);
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
