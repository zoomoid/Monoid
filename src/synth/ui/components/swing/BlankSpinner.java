package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

public class BlankSpinner extends JSpinner {

    public BlankSpinner(){
        super();
        //setBorder(new LineBorder(Color.BLACK));
        setEditor(new BlankSpinnerEditor(this));
        setUI(new BlankSpinnerUI());
        setBorder(BorderFactory.createEmptyBorder());
    }

    public BlankSpinner(SpinnerModel m){
        super(m);
        //setBorder(new LineBorder(Color.BLACK));
        setEditor(new BlankSpinnerEditor(this));
        setUI(new BlankSpinnerUI());
        setBorder(BorderFactory.createEmptyBorder());
    }

    private class BlankSpinnerUI extends BasicSpinnerUI {

        @Override
        protected JComponent createEditor() {
            JComponent c = super.createEditor();
            Dimension s = c.getPreferredSize();
            c.setPreferredSize(new Dimension(s.width, s.height));
            c.setBorder(BorderFactory.createEmptyBorder());
            return c;
        }

        @Override
        protected Component createNextButton() {
            JButton button = (JButton)super.createNextButton();
            setButtonProperties(button);
            return button;
        }

        private void setButtonProperties(JButton button) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            installNextButtonListeners(button);
        }

        @Override
        protected Component createPreviousButton() {
            JButton button = (JButton)super.createPreviousButton();
            setButtonProperties(button);
            return button;
        }
    }

    private class BlankSpinnerEditor extends JLabel implements ChangeListener {

        private JSpinner spinner;

        private Image icon;

        private BlankSpinnerEditor(JSpinner spinner){
            super("", CENTER);
            icon = ((ImageIcon)spinner.getValue()).getImage();
            this.spinner = spinner;
            spinner.addChangeListener(this);
            this.setPreferredSize(new Dimension(60, 40));
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Rectangle d = g.getClipBounds();
            g.setColor(Color.WHITE);
            g.fillRect(d.x, d.y, d.width, d.height);
            g.drawImage(this.icon, d.x, d.y, d.width, d.height, this);
        }

        public void stateChanged(ChangeEvent e){
            icon = ((ImageIcon)spinner.getValue()).getImage();
            repaint();
        }
    }
}
