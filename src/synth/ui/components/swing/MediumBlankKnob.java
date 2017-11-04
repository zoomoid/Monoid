package synth.ui.components.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A blank knob replicating the behaviour of a slider in a basic sense but for usage with lots of
 * other controls close to each other
 */
public class MediumBlankKnob extends BlankKnob {

    private static final int initRadius = 24;

    public MediumBlankKnob(){
        this(0, 100, 0);
    }

    /**
     * Creates a new MediumBlankKnob from the given parameters
     * @param minValue minimum value of the knob
     * @param maxValue maximum value of the knob
     * @param value initial value of the knob
     */
    public MediumBlankKnob(float minValue, float maxValue, float value){
        this(minValue, maxValue, value, 1);
    }

    /**
     * Creates a new MediumBlankKnob from the given parameters
     * @param minValue minimum value of the knob
     * @param maxValue maximum value of the knob
     * @param value initial value of the knob
     * @param scrollFactor factor by which a change on drag gets multiplied. Especially helpful on Knob with large maximum values
     */
    public MediumBlankKnob(float minValue, float maxValue, float value, float scrollFactor){
        this(minValue, maxValue, value, scrollFactor, initRadius, "");
    }

    public MediumBlankKnob(float minValue, float maxValue, float value, float scrollFactor, String label){
        this(minValue, maxValue, value, scrollFactor, initRadius, label);
    }

    private MediumBlankKnob(float minValue, float maxValue, float value, float scrollFactor, int radius, String label){
        super(minValue, maxValue, value, scrollFactor, radius, label);
        setStrokeThickness(5);
    }

    @Override
    public void paintLabel(Graphics2D g){
        drawCenteredString(g, this.label, new Rectangle(0, offset + 2 * radius + 2, 2 * offset + 2*radius, 4 * offset), new Font("Fira Mono", Font.BOLD, 10));
    }
}
