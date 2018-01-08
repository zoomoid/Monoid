package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;

public class BlankTextfield extends JTextField {
    public class BlankTextfieldUI extends BasicTextFieldUI {
        public BlankTextfieldUI(BlankTextfield c){
            c.setBackground(Color.WHITE);
            c.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            c.setForeground(Color.BLACK);
            c.setFont(new Font("Fira Mono", Font.BOLD, 12));
            c.setMinimumSize(new Dimension(100, c.getFontMetrics(c.getFont()).getHeight() + 12));
            c.setPreferredSize(new Dimension(100, c.getFontMetrics(c.getFont()).getHeight() + 12));
        }
    }

    public BlankTextfield(){
        super();
        this.setUI(new BlankTextfieldUI(this));
    }

    public BlankTextfield(String text){
        super(text);
        this.setUI(new BlankTextfieldUI(this));
    }
}
