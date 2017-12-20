package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

public class BlankSpinner extends JSpinner {

    public BlankSpinner(){
        super();
        setEditor(new BlankSpinner.BlankSpinnerEditor(this));
        setUI(new BlankSpinner.BlankSpinnerUI());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    public BlankSpinner(SpinnerModel m){
        super(m);
        setEditor(new BlankSpinner.BlankSpinnerEditor(this));
        setUI(new BlankSpinner.BlankSpinnerUI());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }



    protected class BlankSpinnerUI extends BasicSpinnerUI {

        @Override
        protected JComponent createEditor() {
            JComponent c = super.createEditor();
            c.setPreferredSize(new Dimension(60, 40));
            c.setBorder(BorderFactory.createEmptyBorder());
            return c;
        }

        @Override
        protected Component createNextButton() {
            JButton button = (JButton)super.createNextButton();
            setButtonProperties(button);
            return button;
        }

        protected void setButtonProperties(JButton button) {
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            button.setFocusPainted(false);
        }

        @Override
        protected Component createPreviousButton() {
            JButton button = (JButton)super.createPreviousButton();
            setButtonProperties(button);
            return button;
        }
    }

    protected class BlankSpinnerEditor extends JLabel implements ChangeListener {

        private JSpinner spinner;
        private BlankSpinnerEditor(JSpinner spinner){
            super(spinner.getValue().toString(), CENTER);
            this.spinner = spinner;
            this.spinner.addChangeListener(this);
            this.spinner.setBackground(Color.WHITE);
            this.setFont(new Font("Fira Mono", Font.BOLD, 18));
            this.setPreferredSize(new Dimension(60, 40));
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            this.setText(spinner.getValue().toString());
        }


    }
}
