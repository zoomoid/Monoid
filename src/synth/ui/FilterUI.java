package synth.ui;

import synth.filter.Filter;
import synth.ui.components.swing.LargeBlankKnob;
import synth.ui.components.swing.MediumBlankKnob;
import synth.ui.components.swing.SmallBlankKnob;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FilterUI {

    public JPanel pane;
    private JPanel smallPane, qGainPane;
    private GridLayout grid, optionGrid;
    private LargeBlankKnob frequencyKnob;
    private MediumBlankKnob resonanceKnob;
    private SmallBlankKnob qKnob;
    private SmallBlankKnob gainKnob;

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
        /**
         * 27.4955 is converted from the start frequency of 1000Hz to
         */
        frequencyKnob = new LargeBlankKnob(0.1f, 1000, 27.4955f, 5, "Frequency");
        frequencyKnob.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                double maxX = frequencyKnob.getMaxValue();
                double maxY = 22000;
                double minX = frequencyKnob.getMinValue();
                double minY = 20;
                double b = Math.log(minY/maxY)/(minX-maxX);
                double a = minY / Math.exp(b * minX);
                float x = (float)evt.getNewValue();
                associatedFilter.setCutoff((float)(a * Math.exp(b * x)));
            }
        });
        resonanceKnob = new MediumBlankKnob(0f, 4f, 1f,0.1f, "Resonance");
        qKnob = new SmallBlankKnob(0f, 5f, 2f, 0.4f, "Q");
        qKnob.setSnapToTicks(true);
        qKnob.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // 0 = 6db, 1 = 12db, 2 = 24db; 3 = 48db; 4 = 96db; 5 = 128db;
                int quantizedInput = Math.round((float)evt.getNewValue());
                System.out.println(quantizedInput);
                switch(quantizedInput){
                    case 0 : associatedFilter.setQ(6f); break;
                    case 1 : associatedFilter.setQ(12f); break;
                    case 2 : associatedFilter.setQ(24f); break;
                    case 3 : associatedFilter.setQ(48f); break;
                    case 4 : associatedFilter.setQ(96f); break;
                    case 5 : associatedFilter.setQ(128f); break;
                    default: associatedFilter.setQ(24f); break;
                }
            }
        });
        gainKnob = new SmallBlankKnob(0f, 1f, 0.5f, 0.01f, "Gain");
        gainKnob.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                associatedFilter.setGain((float)(evt.getNewValue()));
            }
        });
        pane.setLayout(grid);
        pane.add(frequencyKnob);
        pane.add(smallPane);
        smallPane.setLayout(optionGrid);
        smallPane.add(resonanceKnob);
        smallPane.add(qGainPane);
        qGainPane.add(qKnob);
        qGainPane.add(gainKnob);
    }
}
