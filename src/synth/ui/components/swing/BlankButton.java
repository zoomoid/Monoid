package synth.ui.components.swing;

import org.w3c.dom.css.Rect;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public class BlankButton extends JButton {

    public BlankButton(){
        super();
        setLayout();
    }
    public BlankButton(Action a){
        super(a);
        setLayout();
    }
    public BlankButton(Icon icon){
        super(icon);
        setLayout();
    }
    public BlankButton(String text){
        super(text);
        setLayout();
    }
    public BlankButton(String text, Icon icon){
        super(text, icon);
        setLayout();
    }

    public class BlankButtonUI extends BasicButtonUI {

        public BlankButtonUI(){
            super();
        }

        @Override
        public void paint(Graphics g, JComponent c){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AbstractButton b = (AbstractButton) c;
            Dimension d = b.getSize();

            Rectangle t = g.getClipBounds();
            FontMetrics fm = g.getFontMetrics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0,0,t.width,t.height);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g.drawRoundRect(2, 2, t.width-4, t.height-4, 5, 5);
            drawCenteredString(g, b.getText(), t, new Font(Font.SANS_SERIF, Font.BOLD, 13));
            g2d.dispose();

        }

        public Dimension getPreferredSize(JComponent c) {
            Dimension d = super.getPreferredSize(c);
            d.setSize(d.width, d.height);
            return d;
        }

        /**
         * Draw a String centered in the middle of a Rectangle.
         *
         * @param g The Graphics instance.
         * @param text The String to draw.
         * @param rect The Rectangle to center the text in.
         */
        public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
            // Get the FontMetrics
            FontMetrics metrics = g.getFontMetrics(font);
            // Determine the X coordinate for the text
            int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
            // Set the font
            g.setFont(font);
            // Draw the String
            g.drawString(text, x, y);
        }
    }

    private void setLayout(){
        setFocusable(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setUI(new BlankButtonUI());
    }
}
