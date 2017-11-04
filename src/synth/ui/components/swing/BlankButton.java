package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class BlankButton extends JButton {

    public BlankButton(){
        super();
    }
    public BlankButton(Action a){
        super(a);
    }
    public BlankButton(Icon icon){
        super(icon);
    }
    public BlankButton(String text){
        super(text);
    }
    public BlankButton(String text, Icon icon){
        super(text, icon);
    }

    public class BlankButtonUI extends ComponentUI {
        @Override
        public void paint(Graphics g, JComponent c){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Rectangle t = c.getBounds(null);
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(t.x, t.y, t.width, t.height, 5, 5);
            //String text = (JButton) c.getText();

        }
    }


}
