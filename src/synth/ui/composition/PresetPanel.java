package synth.ui.composition;

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
            } else {
                if(currentUI != null) {
                    currentUI.close();
                }
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
            } else {
                if(currentUI != null) {
                    currentUI.close();
                }
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
            } else {
                if(currentUI != null) {
                    currentUI.close();
                }
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
            } else {
                if(currentUI != null) {
                    currentUI.close();
                }
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

    public BlankToggleButton oscButton() {
        return oscButton;
    }

    public BlankToggleButton filterButton() {
        return filterButton;
    }

    public BlankToggleButton additivButton() {
        return additivButton;
    }

    public BlankToggleButton modulationButton() {
        return modulationButton;
    }
}
