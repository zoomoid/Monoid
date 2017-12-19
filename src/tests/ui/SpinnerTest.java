package tests.ui;

import synth.ui.components.swing.BlankSpinner;

import javax.swing.*;

public class SpinnerTest {

    public class WaveType {
        private final String img;
        private final String name;
        public WaveType(String img, String name){
            if(img != null && name != null){
                this.img = img;
                this.name = name;
            } else {
                throw new NullPointerException();
            }
        }
        public String getName(){
            return this.name;
        }

        public String getImage(){
            return this.img;
        }
        public String toString(){
            return this.img;
        }
    }

    JPanel pane;
    BlankSpinner spinner;
    public SpinnerTest(){
        pane = new JPanel();
        ImageIcon[] icons = {
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\sine.png", "sine"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\triangle.png", "triangle"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\saw.png", "saw"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\square.png", "square"),
            new ImageIcon("C:\\Users\\Occloxium\\IdeaProjects\\Monoid\\src\\synth\\assets\\src\\noise.png", "noise")
        };
        spinner = new BlankSpinner(new SpinnerListModel(icons));
        pane.add(spinner);
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

