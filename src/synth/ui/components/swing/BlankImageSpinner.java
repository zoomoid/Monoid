package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class BlankImageSpinner extends BlankSpinner {

    public BlankImageSpinner(){
        super();
        setEditor(new BlankImageSpinnerEditor(this));
        setUI(new BlankImageSpinnerUI());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    public BlankImageSpinner(SpinnerModel m){
        super(m);
        setEditor(new BlankImageSpinnerEditor(this));
        setUI(new BlankImageSpinnerUI());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    private class BlankImageSpinnerUI extends BlankSpinnerUI {

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
            return super.createNextButton();
        }

        @Override
        protected Component createPreviousButton() {
            return super.createPreviousButton();
        }
    }

    private class BlankImageSpinnerEditor extends JLabel implements ChangeListener {

        private JSpinner spinner;

        private Image icon;

        private BlankImageSpinnerEditor(JSpinner spinner){
            super("", CENTER);
            icon = ((SpinnerImageContainer)spinner.getValue()).getIcon();
            this.spinner = spinner;
            spinner.addChangeListener(this);
            this.setPreferredSize(new Dimension((int)((450.0/292.0)*40), 40));
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Rectangle d = g.getClipBounds();
            g.setColor(Color.WHITE);
            g.fillRect(d.x, d.y, d.width, d.height);
            g.drawImage(this.icon, d.x + (d.width / 2) - ((int)((450.0/292.0)*40) / 2), d.y, (int)((450.0/292.0)*40), 40, this);
        }

        public void stateChanged(ChangeEvent e){
            icon = ((SpinnerImageContainer)spinner.getValue()).getIcon();
            repaint();
        }
    }
}
