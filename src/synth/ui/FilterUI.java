package synth.ui;

import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankSlider;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

public class FilterUI {

    public JPanel pane;
    private JPanel smallPane, qGainPane;
    private GridLayout grid, optionGrid;
    private BlankKnob frequencyKnob;
    private BlankKnob resonanceKnob;
    private BlankKnob gainKnob;
    /*
    TODO Rework FilterType Slider with a new blank component which displays the basic waveform and can be changed by dragging up and down like on a knob
     */
    private BlankSlider filterType;
    private Filter associatedFilter;
    /*
    TODO Adjust FilterUI to be a lot more dense and more controllable
     */
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
        filterType = new BlankSlider(0, 3, 0);
        filterType.setSnapToTicks(true);
        Hashtable labelTable = new Hashtable();
        labelTable.put(0, new JLabel("LP"));
        labelTable.put(1, new JLabel("HP"));
        labelTable.put(2, new JLabel("BP"));
        labelTable.put(3, new JLabel("NOTCH"));
        filterType.setLabelTable(labelTable);
        filterType.setPaintTicks(true);
        filterType.setPaintLabels(true);
        pane.add(filterType);
        filterType.addChangeListener(e->{
            int value = ((JSlider)e.getSource()).getValue();
            switch (value){
                case 0:
                    associatedFilter.setFilterType(BiquadFilter.BUTTERWORTH_LP);
                    break;
                case 1:
                    associatedFilter.setFilterType(BiquadFilter.BUTTERWORTH_HP);
                    break;
                case 2:
                    associatedFilter.setFilterType(BiquadFilter.BP_PEAK);
                    break;
                case 3:
                    associatedFilter.setFilterType(BiquadFilter.NOTCH);
                    break;
                default: break;
            }
        });
        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1f, false, false), BlankKnob.LARGE, 1000, "Freq");
        frequencyKnob.addPropertyChangeListener(evt -> {
            associatedFilter.setCutoff(frequencyKnob.params().scale(20, 22000, (float)evt.getNewValue()));
        });
        resonanceKnob = new BlankKnob(new BlankKnob.Parameters(0.5f, (float)(8*Math.sqrt(2)), 0.01f, false, true, Math.sqrt(2)), BlankKnob.MEDIUM,1f,"Res");
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
    }
}
