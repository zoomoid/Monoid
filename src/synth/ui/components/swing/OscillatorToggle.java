package synth.ui.components.swing;

import synth.osc.Oscillator;
import synth.ui.OscillatorUI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class OscillatorToggle extends JButton {

    private class OscillatorToggleUI extends BasicButtonUI {

        private final Dimension preferredSize;

        private OscillatorToggleUI(JComponent c){
            preferredSize = new Dimension(160, 80);
            c.setPreferredSize(preferredSize);
            c.setMinimumSize(preferredSize);
            c.setFocusable(true);
            ((JButton)c).setFocusPainted(false);
            ((JButton)c).setBorderPainted(false);
        }
        @Override
        public void paint(Graphics g, JComponent c) {
            //super.paint(g, c);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle t = g.getClipBounds();
            g.setColor(Color.RED);
            g.fillRoundRect(t.x, t.y, t.width, t.height, 5, 5);
            g.setColor(Color.WHITE);
            // Label
            Font title = new Font("Fira Mono", Font.BOLD, 16);
            Font subtitle = new Font("Fira Mono", Font.PLAIN, 12);
            FontMetrics metrics = g.getFontMetrics(title);
            int x = (t.width - metrics.stringWidth(c.getName())) / 2;
            int y = ((t.height - metrics.getHeight()) / 2) + metrics.getAscent();
            g.setFont(title);
            g.drawString(c.getName(), x, y);
            metrics = g.getFontMetrics(subtitle);
            x = t.x + (t.width - metrics.stringWidth(((OscillatorToggle) c).getType())) / 2;
            y += metrics.getHeight();
            g.setFont(subtitle);
            g.drawString(((OscillatorToggle) c).getType(), x, y);
        }
    }

    private boolean isOscUIVisible;
    private Oscillator associatedOscillator;
    private OscillatorUI associatedOscillatorUI;

    public OscillatorToggle(Oscillator associatedOscillator){
        this.isOscUIVisible = false;
        this.associatedOscillator = associatedOscillator;
        this.associatedOscillatorUI = new OscillatorUI(associatedOscillator);
        this.setUI(new OscillatorToggleUI(this));
        this.addActionListener(e -> {
            OscillatorToggle b = ((OscillatorToggle)e.getSource());
            if(b.oscUIVisible()){
                // currently visible
                b.hideOscillatorUI();
            } else {
                // currently invisible
                b.showOscillatorUI();
            }
        });
    }

    public boolean oscUIVisible() {
        return isOscUIVisible;
    }

    private void showOscillatorUI(){
        this.isOscUIVisible = true;
        this.associatedOscillatorUI.show();
    }

    private void hideOscillatorUI(){
        this.isOscUIVisible = false;
        this.associatedOscillatorUI.hide();
    }

    public String getType(){
        if(associatedOscillator != null){
            return this.associatedOscillator.getType();
        } else {
            return "Unknown Oscillator";
        }
    }

    public String getName(){
        if(associatedOscillator != null){
            return this.associatedOscillator.getName();
        } else {
            return "Unnamed Oscillator";
        }
    }
}
