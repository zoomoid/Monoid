package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;

public class BlankTextfield extends JTextField {
    public class BlankTextfieldUI extends BasicTextFieldUI {
        public BlankTextfieldUI(BlankTextfield c){
            c.setBackground(Color.WHITE);
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
