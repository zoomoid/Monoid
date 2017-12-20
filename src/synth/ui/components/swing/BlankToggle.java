package synth.ui.components.swing;

import javax.swing.*;
import java.awt.*;

public class BlankToggle extends JPanel {

    private BlankToggleButton toggle;
    private JLabel label;

    public BlankToggle(String labelText, boolean checked){
        toggle = new BlankToggleButton("");
        toggle.toggle(checked);
        Dimension d = toggle.getPreferredSize();
        toggle.setPreferredSize(new Dimension(d.height, d.height));
        label = new JLabel(labelText);
        label.setFont(new Font("Fira Mono", Font.BOLD, 12));
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(toggle);
        this.add(label);
    }

    public BlankToggle(){
        this("", false);
    }

    public BlankToggle(boolean checked){
        this("", checked);
    }

    public BlankToggle(String labelText){
        this(labelText, false);
    }
}
