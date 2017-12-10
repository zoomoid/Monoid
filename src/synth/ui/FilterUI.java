package synth.ui;

import synth.filter.Filter;
import synth.ui.components.swing.BlankKnob;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FilterUI {

    public JPanel pane;
    private JPanel smallPane, qGainPane;
    private GridLayout grid, optionGrid;
    private BlankKnob frequencyKnob;
    private BlankKnob resonanceKnob;
    private BlankKnob gainKnob;

    private Filter associatedFilter;

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
        frequencyKnob = new BlankKnob(new BlankKnob.Parameters(1, 1000, 1f, false, false), BlankKnob.LARGE, 24.985f, "Frequency");
        frequencyKnob.addPropertyChangeListener(evt -> associatedFilter.setCutoff(frequencyKnob.params().scale(20, 220000, (float)evt.getNewValue())));
        resonanceKnob = new BlankKnob(new BlankKnob.Parameters(0, 4, 0.1f, false, true), BlankKnob.MEDIUM,1f,"Resonance");
        gainKnob = new BlankKnob(new BlankKnob.Parameters(0, 1, 0.01f, false, true), BlankKnob.SMALL,0.5f, "Gain");
        gainKnob.addPropertyChangeListener(evt -> associatedFilter.setGain((float)(evt.getNewValue())));
        pane.setLayout(grid);
        pane.add(frequencyKnob);
        pane.add(smallPane);
        smallPane.setLayout(optionGrid);
        smallPane.add(resonanceKnob);
        smallPane.add(qGainPane);
        qGainPane.add(gainKnob);
    }
}
