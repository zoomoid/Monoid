package synth.ui.components.swing;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * A blank knob replicating the behaviour of a slider in a basic sense but for usage with lots of
 * other controls close to each other
 */
public class SmallBlankKnob extends BlankKnob {

    private final static int initRadius = 12;

    public SmallBlankKnob(){
        this(0, 100, 0);
    }

    /**
     * Creates a new SmallBlankKnob from the given parameters
     * @param minValue minimum value of the knob
     * @param maxValue maximum value of the knob
     * @param value initial value of the knob
     */
    public SmallBlankKnob(float minValue, float maxValue, float value){
        this(minValue, maxValue, value, 1);
    }

    /**
     * Creates a new SmallBlankKnob from the given parameters
     * @param minValue minimum value of the knob
     * @param maxValue maximum value of the knob
     * @param value initial value of the knob
     * @param scrollFactor factor by which a change on drag gets multiplied. Especially helpful on Knob with large maximum values
     */
    public SmallBlankKnob(float minValue, float maxValue, float value, float scrollFactor){
        this(minValue, maxValue, value, scrollFactor, initRadius, "");
    }
    public SmallBlankKnob(float minValue, float maxValue, float value, float scrollFactor, String label){
        this(minValue, maxValue, value, scrollFactor, initRadius, label);

    }

    private SmallBlankKnob(float minValue, float maxValue, float value, float scrollFactor, int radius, String label){
        super(minValue, maxValue, value, scrollFactor, radius, label);
        setStrokeThickness(4);
    }

    @Override
    public void paintLabel(Graphics2D g){
        drawCenteredString(g, this.label, new Rectangle(0, offset + 2 * radius + 1, 2 * offset + 2*radius, 4 * offset), new Font("Fira Mono", Font.BOLD, 10));
    }

}
