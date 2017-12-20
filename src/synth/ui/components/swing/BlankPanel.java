package synth.ui.components.swing;

import javax.swing.*;
import java.awt.*;

public class BlankPanel extends JPanel {

    public BlankPanel(){
        super();
        this.setBackground(Color.WHITE);
    }

    public BlankPanel(boolean isDoubleBuffered){
        super(isDoubleBuffered);
        this.setBackground(Color.WHITE);
    }

    public BlankPanel(LayoutManager layout){
        super(layout);
        this.setBackground(Color.WHITE);
    }

    public BlankPanel(LayoutManager layout, boolean isDoubleBuffered){
        super(layout, isDoubleBuffered);
        this.setBackground(Color.WHITE);
    }


}
