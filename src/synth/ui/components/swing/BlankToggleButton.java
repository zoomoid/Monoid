package synth.ui.components.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public class BlankToggleButton extends BlankButton {

    private boolean isToggled;

    public class BlankToggleButtonUI extends BlankButtonUI {

        public BlankToggleButtonUI(){
            setFont(new Font("Fira Mono", Font.BOLD, 12));
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
            if(((BlankToggleButton)c).isToggled()){
                g.setColor(Color.BLACK);
                g.fillRoundRect(x, y, width, height, 5, 5);
                g2d.setStroke(new BasicStroke(2));
                g.drawRoundRect(x, y, width, height, 5, 5);
                g.setColor(Color.WHITE);
                drawCenteredString(g, b.getText(), t, c.getFont());
            } else {
                g.setColor(Color.WHITE);
                g.fillRoundRect(x, y, width, height, 5, 5);
                g.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g.drawRoundRect(x, y, width, height, 5, 5);
                drawCenteredString(g, b.getText(), t, c.getFont());
            }
            g.dispose();

        }
    }

    public BlankToggleButton(){
        super();
        this.isToggled = false;
        setLayout();
        setInitialListener();
    }
    public BlankToggleButton(Action a){
        super(a);
        this.isToggled = false;
        setLayout();
        setInitialListener();
    }
    public BlankToggleButton(Icon icon){
        super(icon);
        this.isToggled = false;
        setLayout();
        setInitialListener();
    }
    public BlankToggleButton(String text){
        super(text);
        this.isToggled = false;
        setLayout();
        setInitialListener();
    }
    public BlankToggleButton(String text, Icon icon){
        super(text, icon);
        this.isToggled = false;
        setLayout();
        setInitialListener();
    }

    @Override
    protected void setLayout(){
        setFocusable(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setUI(new BlankToggleButtonUI());
    }

    public boolean isToggled(){
        return this.isToggled;
    }

    /**
     * Shortcut for toggle(!toggle.isToggled);
     */
    public void toggle(){
        this.isToggled = !this.isToggled;
        repaint();
    }

    public void toggle(boolean toggleValue){
        this.isToggled = toggleValue;
        repaint();
    }

    protected void setInitialListener(){
        this.addActionListener(e -> ((BlankToggleButton)e.getSource()).toggle());
    }
}
