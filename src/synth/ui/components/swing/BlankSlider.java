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
            g2d.setColor(Color.BLACK);
            g2d.fillRoundRect(t.x, t.y, t.width, t.height, 2, 2);
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(t.x+2,t.y+2, t.width-4, t.height-4, 2,2);
        }
        @Override
        public void paintThumb(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle t = thumbRect;
            g2d.setColor(Color.BLACK);
            if(slider.getOrientation() == 0){
                g2d.fillRoundRect(t.x+2, t.y, 5, t.height, 2, 2);
            } else {
                g2d.fillRoundRect(t.x, t.y+2, t.width, 5, 2, 2);
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

    private double scrollFactor;
    private boolean isLinearScale;
    private double yMin, yMax;

    private BlankLabel label;

    public BlankSlider(){
        this.setLayout();
        this.initialize();
    }
    public BlankSlider(BoundedRangeModel brm){
        super(brm);
        this.setLayout();
        this.initialize();
    }

    public BlankSlider(int orientation){
        super(orientation);
        setLayout();
        this.initialize();
    }

    public BlankSlider(int min, int max){
        super(min, max);
        this.setLayout();
        this.initialize();
    }

    public BlankSlider(int min, int max, int value){
        super(min, max, value);
        this.setLayout();
        this.initialize();
    }

    public BlankSlider(int orientation, int min, int max, int value){
        super(orientation, min, max, value);
        this.setLayout();
        this.initialize();
    }

    private void initialize(){
        this.scrollFactor = 1;
        this.isLinearScale = true;
        this.yMin = 0;
        this.yMax = 0;
    }

    private void setLayout(){
        setFocusable(false);
        setUI(new BlankSliderUI(this));
    }

    public void setScrollFactor(double scrollFactor){
        this.scrollFactor = scrollFactor;
    }

    public double getScrollFactor(){
        return this.scrollFactor;
    }

    public boolean isLinearScale(){
        return isLinearScale;
    }

    public void setLabel(BlankLabel label){
        if(label != null){
            this.label = label;
        }
    }

    public BlankLabel getLabel(){
        return this.label;
    }
}

