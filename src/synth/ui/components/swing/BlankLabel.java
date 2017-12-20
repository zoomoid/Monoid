package synth.ui.components.swing;

import javax.swing.*;
import java.awt.*;

public class BlankLabel extends JLabel {
    public BlankLabel(){
        super();
        _init();
    }

    public BlankLabel(Icon image){
        super(image);
        _init();
    }

    public BlankLabel(Icon image, int horizontalAlignment){
        super(image, horizontalAlignment);
        _init();
    }

    public BlankLabel(String text){
        super(text);
        _init();
    }

    public BlankLabel(String text, Icon icon, int horizontalAlignment){
        super(text, icon, horizontalAlignment);
        _init();
    }

    public BlankLabel(String text, int horizontalAlignment){
        super(text, horizontalAlignment);
        _init();
    }

    private void _init(){
        setFont(new Font("Fira Mono", Font.BOLD, 12));
        setBackground(Color.WHITE);
    }
}
