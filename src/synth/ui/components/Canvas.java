package synth.ui.components;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
    private float[][] samples;

    public final static int BUFFER_LENGTH = 8192;
    public final static int CHANNELS = 2;

    public final static int PREFFERED_WIDTH = 1000;

    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle r = g.getClipBounds();
        int zeroCrossing = r.height / 2;
        int rangeHeight = r.height / 3;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(r.x, r.y, r.width, r.height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, zeroCrossing, r.width, 1);
        Point prev = new Point(0, zeroCrossing);
        // TODO this messes up antialiasing due to integer flooring
        // double compensation = ((float)PREFFERED_WIDTH/BUFFER_LENGTH);
        double compensation = 1;
        for(int i = 0; i < samples.length; i++){
            for(int k = 0; k < BUFFER_LENGTH; k++){
                int x1 = (int)(compensation * k);
                int y1 = (int)(zeroCrossing + (rangeHeight * samples[i][k]));
                if(k == 0){
                    g2d.drawLine(x1, y1, x1, y1);
                } else {
                    g2d.drawLine(prev.x, prev.y, x1, y1);
                }
                prev.x = x1;
                prev.y = y1;
            }
        }
    }

    public Canvas(){
        super();
        this.samples = new float[2][BUFFER_LENGTH];
        this.setPreferredSize(new Dimension(PREFFERED_WIDTH, 250));
    }

    public Canvas(float[][] samples){
        super();
        this.samples = new float[CHANNELS][BUFFER_LENGTH];
        int i, j;
        for(i = 0; i < samples.length && i < CHANNELS; i++){
            for(j = 0; j < samples[i].length && j < BUFFER_LENGTH; j++){
                this.samples[i][j] = samples[i][j];
            }
        }
        this.setPreferredSize(new Dimension(PREFFERED_WIDTH, 250));
    }

    public void update(float[][] samples){
        // if samples is not the right size, shrink it
        int i, j;
        for(i = 0; i < samples.length && i < CHANNELS; i++){
            for(j = 0; j < samples[i].length && j < BUFFER_LENGTH; j++){
                this.samples[i][j] = samples[i][j];
            }
        }
        this.repaint();
    }
}
