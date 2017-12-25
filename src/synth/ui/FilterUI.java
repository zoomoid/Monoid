package synth.ui;

import synth.SynthController;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

public class FilterUI {

    public class FilterType extends SpinnerImageContainer {

        public FilterType(ImageIcon img, int index){
            if(img == null){
                throw new IllegalArgumentException("FilterType expects icon to be not null");
            }
            this.index = index;
            this.img = img.getImage();
        }

        public FilterType(String path, int index){
            if(path == null){
                throw new IllegalArgumentException("FilterType expects path to be not null");
            }
            this.img = new ImageIcon((SynthController.class.getResource("assets/" + path)).getPath()).getImage();
            if(this.img == null){
                throw new IllegalArgumentException("Cannot load image from given path");
            }
            this.index = index;
        }

        public FilterType(String path){
            if(path == null){
                throw new IllegalArgumentException("FilterType expects path to be not null");
            }
            this.img = new ImageIcon((SynthController.class.getResource("assets/" + path)).getPath()).getImage();
            if(this.img == null){
                throw new IllegalArgumentException("Cannot load image from given path");
            }
            this.index = staticIndex;
            staticIndex++;
        }
    }

    public JPanel pane;
    private JPanel smallPane, qGainPane;
    private GridLayout grid, optionGrid;
    private BlankKnob frequencyKnob;
    private BlankKnob resonanceKnob;
    private BlankKnob gainKnob;
    private BlankImageSpinner filterType;
    private Filter associatedFilter;

    // TODO Adjust FilterUI to be a lot more dense and more controllable
    JFrame ui;
    public FilterUI(Filter associatedFilter){
        this.associatedFilter = associatedFilter;
        pane = new JPanel();
        pane.setBackground(Color.WHITE);
        smallPane = new JPanel();
        smallPane.setBackground(Color.WHITE);
        qGainPane = new JPanel();
        qGainPane.setBackground(Color.WHITE);
        grid = new GridLayout(1, 2, 5, 5);
        optionGrid = new GridLayout(2, 1, 2, 2);
        FilterType[] icons = {
                new FilterType("src/lpf.png", 0),
                new FilterType("src/hpf.png",1),
                new FilterType("src/bpf.png",2),
        };
        filterType = new BlankImageSpinner(new SpinnerListModel(icons));
        filterType.addChangeListener(e->{
            int value = ((FilterType)((JSpinner)e.getSource()).getValue()).getIndex();
            switch (value){
                case 0:
                    associatedFilter.setFilterType(BiquadFilter.LPF);
                    break;
                case 1:
                    associatedFilter.setFilterType(BiquadFilter.HPF);
                    break;
                case 2:
                    associatedFilter.setFilterType(BiquadFilter.BP_PEAK);
                    break;
                default: break;
            }
        });
        filterType.setAlignmentX(Component.LEFT_ALIGNMENT);
        pane.add(filterType);

        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1f, false, false), BlankKnob.LARGE, 1000, "Cutoff");
        frequencyKnob.addPropertyChangeListener(evt -> {
            associatedFilter.setCutoff(frequencyKnob.params().scale(20, 22000, (float)evt.getNewValue()));
        });
        resonanceKnob = new BlankKnob(new BlankKnob.Parameters(0.5f, (float)(8*Math.sqrt(2)), 0.01f, false, true, Math.sqrt(2)), BlankKnob.MEDIUM,1f,"Q");
        resonanceKnob.addPropertyChangeListener(evt -> {
            associatedFilter.setQ((float)evt.getNewValue());
        });
        gainKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.MEDIUM,1f, "Gain");
        gainKnob.addPropertyChangeListener(evt -> associatedFilter.setGain((float)(evt.getNewValue())));
        pane.setLayout(grid);
        pane.add(frequencyKnob);
        pane.add(smallPane);
        smallPane.setLayout(optionGrid);
        smallPane.add(resonanceKnob);
        smallPane.add(gainKnob);

        this.ui = new JFrame(associatedFilter.getName());
        this.ui.setContentPane(this.pane);
        this.ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ui.pack();
        this.ui.setResizable(false);
    }

    public void hide(){
        this.ui.setVisible(false);
    }

    public void show(){
        this.ui.setVisible(true);
    }
}
