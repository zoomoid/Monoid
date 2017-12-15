package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class BlankSlider extends JSlider {

    private class BlankSliderUI extends BasicSliderUI {

        public BlankSliderUI(JSlider slider){
            super(slider);
            slider.setBackground(Color.WHITE);
        }
        public void paintTrack(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Rectangle t = trackRect;
            if(slider.getOrientation() == 0){
                g2d.setColor(Color.BLACK);
                g2d.drawRect(t.x, t.y, t.width, t.height);
            } else {
                g2d.setStroke(new BasicStroke(1));
                g2d.setColor(Color.BLACK);
                g2d.drawRect(t.x, t.y, t.width, t.height);
            }
        }
        @Override
        public void paintThumb(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle t = thumbRect;
            g2d.setColor(Color.BLACK);
            if(slider.getOrientation() == 0){
                g2d.fillRect(t.x, t.y, 5, t.height);
            } else {
                g2d.fillRect(t.x, t.y, t.width, 5);
            }
        }

        @Override
        protected Color getFocusColor(){
            return new Color(255,255,255);
        }
        @Override
        protected Color getHighlightColor(){
            return new Color(255,255,255);
        }
        @Override
        protected Color getShadowColor(){
            return new Color(255,255,255);
        }

    }

    public BlankSlider(){
        setLayout();
    }
    public BlankSlider(BoundedRangeModel brm){
        super(brm);
        setLayout();
    }

    public BlankSlider(int orientation){
        super(orientation);
        setLayout();
    }

    public BlankSlider(int min, int max){
        super(min, max);
        setLayout();
    }

    public BlankSlider(int min, int max, int value){
        super(min, max, value);
        setLayout();
    }
    public BlankSlider(int orientation, int min, int max, int value){
        super(orientation, min, max, value);
        setLayout();
    }

    private void setLayout(){
        setFocusable(false);
        setUI(new BlankSliderUI(this));
    }
}

