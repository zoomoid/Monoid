package synth.ui.composition;

import synth.ui.components.swing.BlankButton;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankToggleButton;
import synth.ui.providers.*;

import javax.swing.*;

public class PresetPanel extends JFrame {
    private BlankPanel panel;
    private BlankToggleButton oscButton, filterButton, additivButton, modulationButton;

    private Provider currentUI;

    public PresetPanel(String title){
        super(title);
        panel = new BlankPanel();

        oscButton = new BlankToggleButton("Oscillator");
        oscButton.addActionListener(e -> {
            BlankToggleButton b = ((BlankToggleButton)e.getSource());
            if(!b.isToggled()){
                if(currentUI != null) {
                    currentUI.close();
                }
                currentUI = new OscillatorUIProvider();
            }
        });
        filterButton = new BlankToggleButton("Filter");
        filterButton.addActionListener(e -> {
            BlankToggleButton b = ((BlankToggleButton)e.getSource());
            if(!b.isToggled()){
                if(currentUI != null) {
                    currentUI.close();
                }
                currentUI = new FilterUIProvider();
            }
        });
        additivButton = new BlankToggleButton("Additiv");
        additivButton.addActionListener(e -> {
            BlankToggleButton b = ((BlankToggleButton)e.getSource());
            if(!b.isToggled()){
                if(currentUI != null) {
                    currentUI.close();
                }
                currentUI = new AdditiveUIProvider();
            }
        });
        modulationButton = new BlankToggleButton("Modulation");
        modulationButton.addActionListener(e -> {
            BlankToggleButton b = ((BlankToggleButton)e.getSource());
            if(!b.isToggled()){
                if(currentUI != null) {
                    currentUI.close();
                }
                currentUI = new ModulationUIProvider();
            }
        });

        this.panel.add(oscButton);
        this.panel.add(filterButton);
        this.panel.add(additivButton);
        this.panel.add(modulationButton);

        this.setContentPane(this.panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(true);
        this.setVisible(true);
    }
}
