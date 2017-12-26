package synth.ui;

import synth.SynthController;
import synth.filter.Filter;
import synth.filter.models.FilterModel;
import synth.filter.models.MonoMoog;
import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;

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

    public BlankPanel pane;
    private BlankPanel cutoffPane, qGainPane, typePane;
    private BlankKnob frequencyKnob;
    private BlankKnob resonanceKnob;
    private BlankKnob gainKnob;
    private BlankImageSpinner filterType;
    private Filter associatedFilter;

    JFrame ui;
    // TODO Adjust FilterUI to be a lot more dense and more controllable
    public FilterUI(Filter associatedFilter){
        this.associatedFilter = associatedFilter;
        pane = new BlankPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        typePane = new BlankPanel();
        typePane.setLayout(new FlowLayout(FlowLayout.CENTER));
        cutoffPane = new BlankPanel();
        cutoffPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        qGainPane = new BlankPanel();
        qGainPane.setLayout(new FlowLayout(FlowLayout.CENTER));

        pane.add(typePane);
        pane.add(cutoffPane);
        pane.add(qGainPane);

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
                    associatedFilter.setFilterType(FilterModel.Type.LPF);
                    break;
                case 1:
                    associatedFilter.setFilterType(FilterModel.Type.HPF);
                    break;
                case 2:
                    associatedFilter.setFilterType(FilterModel.Type.BPF);
                    break;
                default: break;
            }
        });
        filterType.setAlignmentX(Component.CENTER_ALIGNMENT);

        typePane.add(filterType);

        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1f, false, false), BlankKnob.LARGE, 1000, "Cutoff");
        frequencyKnob.addPropertyChangeListener(e -> {
            associatedFilter.setCutoff(frequencyKnob.params().scale(20, 22000, (float)e.getNewValue()));
        });

        cutoffPane.add(frequencyKnob);
        if(associatedFilter.getType() == Filter.Type.MonoMoog){
            resonanceKnob = new BlankKnob(new BlankKnob.Parameters(0.5f, (float)(8*Math.sqrt(2)), 0.01f, false, true, Math.sqrt(2)), BlankKnob.MEDIUM,1f,"Resonance");
            resonanceKnob.addPropertyChangeListener(e -> {
                associatedFilter.setQ((float)e.getNewValue());
            });
            qGainPane.add(resonanceKnob);
        }

        gainKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.MEDIUM,1f, "Gain");
        gainKnob.addPropertyChangeListener(e -> {associatedFilter.setGain((float)(e.getNewValue()));});

        qGainPane.add(gainKnob);

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
