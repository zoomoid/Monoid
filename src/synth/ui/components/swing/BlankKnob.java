package synth.ui.components.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BlankKnob extends JComponent implements MouseListener, MouseMotionListener {
    /**
     * Constant holding the knob size parameters
     */
    public static class Size {
        protected final int radius;
        protected final int offset;
        public Size(int radius, int offset){
            this.radius = radius;
            // this.size.offset shall not be bigger than this.size.radius
            this.offset = Math.min(offset, this.radius);
        }
        protected int radius(){
            return this.radius;
        }
        protected int offset(){
            return this.offset;
        }
    }

    public static final Size SMALL = new Size(12, 6);

    public static final Size MEDIUM = new Size(24, 8);

    public static final Size LARGE = new Size(36, 12);

    public static class Parameters {
        protected float min;
        protected float max;
        protected float scrollFactor;
        protected boolean snapToTicks;
        protected boolean isLinearScale;
        public Parameters(float min, float max, float scrollFactor, boolean snapToTicks, boolean isLinearScale){
            this.min = min;
            this.max = max;
            this.scrollFactor = scrollFactor;
            this.isLinearScale = isLinearScale;
            this.snapToTicks = snapToTicks;
        }
        public float scale(float foreignMin, float foreignMax, float foreignValue){
            if(this.isLinearScale){
                return foreignValue;
            } else {
                double b = Math.log(foreignMin / foreignMax)/(this.min - this.max);
                double a = foreignMin / Math.exp(b * this.min);
                return ((float)(a * Math.exp(b * foreignValue)));
            }
        }
    }

    public static final Parameters DEFAULT = new Parameters(0, 100, 1, false, true);

    protected int strokeCircle;
    protected int strokeIndicator;

    protected int dimensionWidth;
    protected int dimensionHeight;

    /** Holding the rotational this.size.offset */
    protected double theta;
    /** Background color for the knob */
    protected Color background;
    /** Knob outline stroke and indicator line color */
    protected Color knob;

    /** Size object containing basic graphical variables */
    protected Size size;

    /** Parameters object containing necessary values for the knob */
    protected Parameters params;
    /** Containing the current state of the mouse */
    protected boolean pressed;
    /** Point used to calculate whether a MouseClicked event is in the area of the knob to be dragged */
    protected Point mousePt;
    /** Contains the previous state from where to pick up a drag again */
    protected float prev;

    /**  Holds the current value */
    protected float value;
    /** value property listener */
    private PropertyChangeSupport valueChange = new PropertyChangeSupport(this);
    /** Holds the maximum value */
    protected float maxValue;
    /** Holds the minimum value */
    protected float minValue;
    /** Holds the scrollFactor */
    protected float scrollFactor;
    /** Snap knob to integer ticks */
    protected boolean snapToTicks = false;
    /** Label text */
    protected String label;

    /**
     *
     * @param params
     * @param size
     * @param label
     */
    public BlankKnob(Parameters params, Size size, float value, String label){
        if(size != null){
            this.size = size;
        } else {
            this.size = new Size(24, 8);
        }

        if(params != null){
            this.params = params;
        } else {
            this.params = new Parameters(0, 100, 1, false, true);
        }

        this.dimensionWidth = 2*this.size.radius + 6*this.size.offset;
        this.dimensionHeight = 2*this.size.radius + 4*this.size.offset;
        this.strokeCircle = this.size.radius / 4;
        this.strokeIndicator = (int)Math.sqrt(this.strokeCircle * 2);

        this.minValue = this.params.min;
        this.maxValue = this.params.max;

        if(value <= maxValue && value >= minValue){
            this.value = value;
        } else {
            value = 0;
        }
        this.label = label;
        this.theta = theta();
        this.scrollFactor = (scrollFactor > 0 ? scrollFactor : 1);
        this.background = Color.WHITE;
        this.knob = Color.BLACK;
        // add default listeners to the knob
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /**
     * Set the stroke thickness of the outer circle
     * By design, the indicator stroke thickness is less than that, per default 2
     * @param thickness width of the line of the outer circle
     */
    public void setStrokeThickness(int thickness) {
        this.strokeCircle = thickness;
        this.strokeIndicator = thickness / 2;
    }

    public float scrollFunction(float x){
        return 1;
    }

    /**
     * Gets the current value of the knob
     * @return the current value of the knob
     */
    public float getValue(){
        return this.value;
    }

    public Parameters params(){
        return params;
    }

    /**
     * Gets the minimum value of the knob
     * @return double containing the minimum value
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * Gets the maximum value of the knob
     * @return double containing the maximum value
     */
    public float getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the current value of the knob if its in range of minValue and maxValue.
     * Also repaints knob on change
     * @param value new current value of the knob
     */
    public void setValue(float value){
        double temp = this.value;
        if(value <= maxValue && value >= minValue){
            this.value = value;
            repaint();
            valueChange.firePropertyChange("value", temp, this.value);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        valueChange.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        valueChange.removePropertyChangeListener(listener);
    }

    /**
     * Set the minimum value of the knob
     * @param minValue minimal value of the knob
     */
    public void setMinValue(float minValue) {
        this.minValue = minValue;
        this.params.min = minValue;
        repaint();
    }

    /**
     * Sets the maximum value of the knob
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        this.params.max = maxValue;
        repaint();
    }

    public void setSnapToTicks(boolean snapToTicks) {
        this.snapToTicks = snapToTicks;
        this.params.snapToTicks = snapToTicks;
        if(snapToTicks){
            this.minValue = (int)this.minValue;
            this.maxValue = (int)this.maxValue;
            this.value = (int)this.value;
        }
        repaint();
    }

    /**
     * Draws the Knob
     * @param g graphics canvas
     */
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle r = g2d.getClipBounds();
        g.setClip(r.x, r.y, dimensionWidth, dimensionHeight);
        // Draw the knob backdrop
        g.setColor(knob);
        g2d.setStroke(new BasicStroke(strokeCircle));
        g.drawOval(this.size.offset,this.size.offset,2*this.size.radius,2*this.size.radius);
        g.setColor(background);
        g2d.setStroke(new BasicStroke(strokeIndicator));
        // get the point on the outer knob circle
        Point pt = getKnobAngle();
        int xc = (int)pt.getX();
        int yc = (int)pt.getY();

        // Draw the knob state indicator.
        g.setColor(knob);
        g2d.drawLine(xc, yc, this.size.radius + this.size.offset, this.size.radius + this.size.offset);

        paintLabel(g2d);
    }

    public void paintLabel(Graphics2D g){
        drawCenteredString(g, this.label, new Rectangle(this.size.offset, this.size.offset + 2 * this.size.radius, 2 * this.size.offset + 2*this.size.radius, 4 * this.size.offset), new Font("Fira Mono", Font.BOLD, 10));
    }

    /**
     * Calculates the point on the knob circle to where to draw the indicator line to
     * @return Point on the knob circle
     */
    protected Point getKnobAngle() {
        // basically tells the internal state theta to be rotated by 5/4 clockwise (could be the same as 3/4 ccw)
        double h = 3 * (theta) + 5.0/4.0 * Math.PI;
        // calculates x and y with basic trigonometric functions
        int xcp = (int)(this.size.radius * Math.sin(h));
        int ycp = (int)(this.size.radius * Math.cos(h));
        // calculate the this.size.offset from knob center
        int xc = this.size.radius + this.size.offset + xcp;
        int yc = this.size.radius + this.size.offset - ycp;
        // Create the new Point
        return new Point(xc,yc);
    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    /**
     * Return the minimum size that the knob would like to be.
     * This is the same size as the preferred size so the
     * knob will be of a fixed size.
     *
     * @return the minimum size of the JKnob.
     */
    public Dimension getMinimumSize() {
        return new Dimension(dimensionWidth, dimensionHeight);
    }

    /**
     * Return the preferred size that the knob would like to be.
     * This is the same size as the minimum size so the
     * knob will be of a fixed size.
     *
     * @return the minimum size of the JKnob.
     */
    public Dimension getPreferredSize() {
        return new Dimension(dimensionWidth, dimensionHeight);
    }

    /**
     * Determine if the mouse click was on the spot or
     * not.  If it was return true, otherwise return
     * false.
     *
     * @return true if x,y is on the spot and false if not.
     */
    private boolean isOnSpot() {
        return (mousePt.distance(new Point(this.size.radius + this.size.offset, this.size.radius + this.size.offset)) < this.size.radius);
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
            float dy = (prev - e.getY()) * this.scrollFactor;
            // check for the value to be in range of minValue and maxValue
            if(this.value + dy <= maxValue && this.value + dy >= minValue){
                this.setValue(this.scrollFunction(this.value + dy) * (this.value + dy));
            } else if (this.value + dy > maxValue){
                this.setValue(maxValue);
            } else if (this.value + dy < minValue){
                this.setValue(minValue);
            }
            if(this.snapToTicks){
                this.setValue((int)this.value);
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
