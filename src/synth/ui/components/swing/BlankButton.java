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

        public int preferredWidth, preferredHeight;

        public BlankButtonUI(){
            super();
            preferredHeight = 30;
            preferredWidth = 110;
            setPreferredSize(new Dimension(preferredWidth, preferredHeight));
            setMinimumSize(new Dimension(preferredWidth, preferredHeight));
            setFont(new Font("Fira Mono", Font.BOLD, 13));
        }

        @Override
        public void paint(Graphics g, JComponent c){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AbstractButton b = (AbstractButton) c;
            Dimension d = b.getSize();

            d = c.getPreferredSize();

            Rectangle t = g.getClipBounds();
            FontMetrics fm = g.getFontMetrics();

            int width = t.width - 5;
            int height = t.height - 5;
            int x = 1;
            int y = 1;

            g2d.setColor(Color.WHITE);
            g.fillRoundRect(x, y, width, height, 5, 5);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, width, height, 5, 5);
            drawCenteredString(g, b.getText(), t, c.getFont());
            g2d.dispose();

        }

        public Dimension getPreferredSize(JComponent c) {
            return super.getPreferredSize(c);
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

    protected void setLayout(){
        setFocusable(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setUI(new BlankButtonUI());
    }

    public void pack(){
        FontMetrics f = this.getFontMetrics(this.getFont());
        this.setPreferredSize(new Dimension(f.stringWidth(this.getText()) + 8, f.getHeight()+4));
    }
}
