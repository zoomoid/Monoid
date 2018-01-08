package synth.ui;

import synth.SynthController;
import synth.filter.Filter;
import synth.filter.models.FilterModel;
import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;

public class FilterUI extends SynthesizerUserInterfaceModule {

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

    public BlankPanel pane, topPane, optionsPane;
    private BlankPanel cutoffPane, qGainPane, typePane;
    private BlankKnob frequencyKnob;
    private BlankKnob resonanceKnob;
    private BlankKnob gainKnob;
    private BlankImageSpinner filterType;
    private Filter associatedFilter;

    private BlankPanel _pane;
    private BlankLabel filterFineTuneLabel, filterLabel;
    private BlankToggleButton optionsButton;
    private BlankLabel _cutoffLabel, _resonanceLabel, _gainLabel, _typeLabel;
    private BlankSpinner _cutoff, _resonance, _gain;
    private BlankImageSpinner _type;

    FilterType[] icons = {
            new FilterType("lpf.png", 0),
            new FilterType("hpf.png",1),
            new FilterType("bpf.png",2),
    };

    public JFrame ui;
    // TODO Adjust FilterUI to be a lot more dense and more controllable
    public FilterUI(Filter associatedFilter){
        this.associatedFilter = associatedFilter;
        pane = new BlankPanel();
        topPane = new BlankPanel();
        filterLabel = new BlankLabel("Filter " + associatedFilter.getName());
        optionsButton = new BlankToggleButton("Options");
        optionsButton.setFont(new Font("Fira Mono", Font.BOLD, 10));
        optionsButton.pack();
        optionsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        optionsButton.addActionListener(e -> {
            if(optionsPane.isVisible()){
                this.hideFineTunePanel();
            } else {
                this.showFineTunePanel();
            }
        });
        topPane.add(filterLabel);
        topPane.add(optionsButton);

        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(topPane);
        typePane = new BlankPanel();
        typePane.setLayout(new FlowLayout(FlowLayout.CENTER));
        cutoffPane = new BlankPanel();
        cutoffPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        qGainPane = new BlankPanel();
        qGainPane.setLayout(new FlowLayout(FlowLayout.CENTER));

        pane.add(typePane);
        pane.add(cutoffPane);
        pane.add(qGainPane);

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

        resonanceKnob = new BlankKnob(new BlankKnob.Parameters(0.1f, 10, 0.1f, false, true), BlankKnob.MEDIUM,(float)(1/Math.sqrt(2)),"Resonance");
        resonanceKnob.addPropertyChangeListener(e -> {
            associatedFilter.setQ((float)e.getNewValue());
        });
        qGainPane.add(resonanceKnob);

        gainKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.MEDIUM,1f, "Gain");
        gainKnob.addPropertyChangeListener(e -> {associatedFilter.setGain((float)(e.getNewValue()));});

        qGainPane.add(gainKnob);

        this.ui = new JFrame(associatedFilter.getName());
        this.ui.setContentPane(this.pane);
        this.ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.ui.setLocation(300, 300);
        this.ui.pack();
        this.ui.setResizable(false);

        this.createFineTunePanel();

        optionsPane = new BlankPanel();
        optionsPane.setLayout(new BoxLayout(optionsPane, BoxLayout.Y_AXIS));
        optionsPane.add(filterFineTuneLabel);
        optionsPane.add(_pane);
        pane.add(optionsPane);
        optionsPane.setVisible(false);
        this.initializeFromFilter();
    }

    public void hide(){
        this.ui.setVisible(false);
    }

    public void show(){
        this.ui.setVisible(true);
    }

    private void showFineTunePanel(){
        optionsPane.setVisible(true);
        this.ui.pack();
    }

    private void hideFineTunePanel(){
        optionsPane.setVisible(false);
        this.ui.pack();
    }

    private void initializeFromFilter(){
        // TODO implement a way to set determine the value of the component by the actual type of the component
        //this.filterType.setValue();
        this.frequencyKnob.setValue(associatedFilter.getCutoff().getValue());
        this.gainKnob.setValue(associatedFilter.getGain().getValue());
    }

    // TODO add failsafe try catch block for when parsing int or float is not successful. Reset to the value in associatedFilter after failure (== NaN)
    private void createFineTunePanel(){
        _pane = new BlankPanel(new GridLayout(4, 2));
        filterFineTuneLabel = new BlankLabel("Fine Tuning");
        this.filterFineTuneLabel.setFont(new Font("Fira Mono", Font.BOLD, 16));

        _cutoffLabel = new BlankLabel("Cutoff");
        _cutoff = new BlankSpinner(new SpinnerNumberModel(associatedFilter.getCutoff().getValue(), 20.0, 22000.0, 1));
        _cutoff.addChangeListener(e -> {
            this.frequencyKnob.setValue(Float.parseFloat(_cutoff.getValue() + ""));
        });

        _resonanceLabel = new BlankLabel("Resonance");
        _resonance = new BlankSpinner(new SpinnerNumberModel(associatedFilter.getQ().getValue(), 0.01, 10.0, 0.1));
        _resonance.addChangeListener(e -> {
            this.resonanceKnob.setValue(Float.parseFloat(_resonance.getValue() + ""));
        });

        _gainLabel = new BlankLabel("Gain");
        _gain = new BlankSpinner(new SpinnerNumberModel(associatedFilter.getGain().getValue(), 0.0,1.0, 0.01));
        _gain.addChangeListener(e -> {
            this.gainKnob.setValue(Float.parseFloat(_gain.getValue() + ""));
        });

        _typeLabel = new BlankLabel("Filter Type");
        _type = new BlankImageSpinner(new SpinnerListModel(icons));

        _pane.add(_cutoffLabel);
        _pane.add(_cutoff);
        _pane.add(_typeLabel);
        _pane.add(_type);
        _pane.add(_resonanceLabel);
        _pane.add(_resonance);
        _pane.add(_gainLabel);
        _pane.add(_gain);
        // TODO implement way to detect change for spinner
        // this._frequency = new BlankTextfield(associatedOscillator.getFrequency().getValue() + "");
        //        this._frequency.addActionListener(e -> {
        //            this.frequencyKnob.setValue(Float.parseFloat(_frequency.getText()));
        //        });
    }

    public Filter getAssociatedDevice(){
        return this.associatedFilter;
    }
}
