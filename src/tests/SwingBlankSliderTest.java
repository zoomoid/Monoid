package tests;

import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;

public class SwingBlankSliderTest {

    private JPanel pane;

    public SwingBlankSliderTest(){
        pane = new JPanel();
        pane.setBackground(Color.WHITE);
        pane.add(new SmallBlankKnob(0, 1000, 0, 1));
        pane.add(new BlankSlider(0, 100, 100));
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("SwingBlankSliderTest");
        frame.setContentPane(new SwingBlankSliderTest().pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
