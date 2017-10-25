package synth.ui.components.swing;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * A blank knob replicating the behaviour of a slider in a basic sense but for usage with lots of
 * other controls close to each other
 */
public class BlankKnob extends JComponent implements MouseListener, MouseMotionListener {

    /**
     * Constant holding the knob radius
     */
    private final int radius = 12;
    /**
     * Holding the rotational offset
     */
    private double theta;
    /**
     * Background color for the knob
     */
    private Color background;
    /**
     * Knob outline stroke and indicator line color
     */
    private Color knob;

    /**
     * Containing the current state of the mouse
     */
    private boolean pressed;
    /**
     * Point used to calculate whether a MouseClicked event is in the area of the knob to be dragged
     */
    private Point mousePt;
    /**
     * Contains the previous state from where to pick up a drag again
     */
    private double prev;

    /**
     * Holds the current value
     */
    protected double value;
    /**
     * Holds the maximum value
     */
    protected double maxValue;
    /**
     * Holds the minimum value
     */
    protected double minValue;
    /**
     * Holds the scrollFactor
     */
    protected double scrollFactor;

    /**
     * Creates a blank knob with default values (0, 100, 0)
     */
    public BlankKnob(){
        this(0, 100, 0);
    }

    /**
     * Creates a new BlankKnob from the given parameters
     * @param minValue minimum value of the knob
     * @param maxValue maximum value of the knob
     * @param value initial value of the knob
     */
    public BlankKnob(double minValue, double maxValue, double value){
        this(minValue, maxValue, value, 1);
    }

    /**
     * Creates a new BlankKnob from the given parameters
     * @param minValue minimum value of the knob
     * @param maxValue maximum value of the knob
     * @param value initial value of the knob
     * @param scrollFactor factor by which a change on drag gets multiplied. Especially helpful on Knob with large maximum values
     */
    public BlankKnob(double minValue, double maxValue, double value, double scrollFactor){
        this.minValue = minValue;
        this.maxValue = maxValue;
        if(value <= maxValue && value >= minValue){
            this.value = value;
        } else {
            value = 0;
        }
        this.theta = theta();
        this.scrollFactor = (scrollFactor > 0 ? scrollFactor : 1);
        this.background = Color.WHITE;
        this.knob = Color.BLACK;
        // add default listeners to the knob
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Gets the current value of the knob
     * @return the current value of the knob
     */
    public double getValue(){
        return this.value;
    }

    /**
     * Gets the minimum value of the knob
     * @return double containing the minimum value
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * Gets the maximum value of the knob
     * @return double containing the maximum value
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the current value of the knob if its in range of minValue and maxValue.
     * Also repaints knob on change
     * @param value new current value of the knob
     */
    public void setValue(double value){
        if(value <= maxValue && value >= minValue){
            this.value = value;
            repaint();
        }
    }

    /**
     * Set the minimum value of the knob
     * @param minValue minimal value of the knob
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
        repaint();
    }

    /**
     * Sets the maximum value of the knob
     * @param maxValue
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        repaint();
    }

    /**
     * Draws the Knob
     * @param g graphics canvas
     */
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the knob backdrop
        g.setColor(knob);
        g2d.setStroke(new BasicStroke(2));
        g.drawOval(1,1,2*radius-2,2*radius-2);
        g.setColor(background);

        // get the point on the outer knob circle
        Point pt = getKnobAngle();
        int xc = (int)pt.getX();
        int yc = (int)pt.getY();

        // Draw the knob state indicator.
        g.setColor(knob);
        g2d.drawLine(xc, yc, radius, radius);
    }

    /**
     * Calculates the point on the knob circle to where to draw the indicator line to
     * @return Point on the knob circle
     */
    private Point getKnobAngle() {
        int r = radius;
        // basically tells the internal state theta to be rotated by 5/4 clockwise (could be the same as 3/4 ccw)
        double h = 3 * (theta) + 5.0/4.0 * Math.PI;
        // calculates x and y with basic trigonometric functions
        int xcp = (int)(r * Math.sin(h));
        int ycp = (int)(r * Math.cos(h));
        // calculate the offset from knob center
        int xc = radius + xcp;
        int yc = radius - ycp;
        // Create the new Point
        return new Point(xc,yc);
    }

    /**
     * Return the ideal size that the knob would like to be.
     *
     * @return the preferred size of the JKnob.
     */
    public Dimension getPreferredSize() {
        return new Dimension(2*radius,2*radius);
    }

    /**
     * Return the minimum size that the knob would like to be.
     * This is the same size as the preferred size so the
     * knob will be of a fixed size.
     *
     * @return the minimum size of the JKnob.
     */
    public Dimension getMinimumSize() {
        return new Dimension(2*radius,2*radius);
    }

    /**
     * Determine if the mouse click was on the spot or
     * not.  If it was return true, otherwise return
     * false.
     *
     * @return true if x,y is on the spot and false if not.
     */
    private boolean isOnSpot() {
        return (mousePt.distance(new Point(radius, radius)) < radius);
    }

    /**
     * Empy method because nothing happens on a click.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse click.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Empty method because nothing happens when the mouse
     * enters the Knob.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse entry.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Empty method because nothing happens when the mouse
     * exits the knob.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse exit.
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * When the mouse button is pressed, the dragging of the
     * spot will be enabled if the button was pressed over
     * the spot.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse press.
     */
    public void mousePressed(MouseEvent e) {
        mousePt = e.getPoint();
        pressed = isOnSpot();
    }

    /**
     * When the button is released, the dragging of the spot
     * is disabled.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse release.
     */
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        prev = mousePt.y;
    }

    // Methods from the MouseMotionListener interface.

    /**
     * Empty method because nothing happens when the mouse
     * is moved if it is not being dragged.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse move.
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Compute the new angle for the spot and repaint the
     * knob.  The new angle is computed based on the new
     * mouse position.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse drag.
     */
    public void mouseDragged(MouseEvent e) {
        if (pressed) {
            double dy = (prev - e.getY()) * this.scrollFactor;
            // check for the value to be in range of minValue and maxValue
            if(this.value + dy <= maxValue && this.value + dy >= minValue){
                this.value += dy;
            } else if (this.value + dy > maxValue){
                this.value = maxValue;
            } else if (this.value + dy < minValue){
                this.value = minValue;
            } else {
                this.value = this.value;
            }
            // calculate the new theta
            theta = this.theta();
            // update previous state variable
            prev = e.getY();
            // repaint the whole knob
            repaint();
        }
    }

    /**
     * Calculates theta by multiplying the value/maxValue ratio with a full rotation
     * @return rotational angle in rad
     */
    private double theta(){
        // calculates theta by multiplying the value/maxValue ratio with a full rotation
        return this.value/this.maxValue * 0.5 * Math.PI;
    }
}
