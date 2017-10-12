package synth.ui.components;

import javax.swing.*;
import java.awt.*;

public class ImageButton extends JButton {
    public ImageButton(String filename){
        super(new ImageIcon(filename));
    }
}
