package synth.ui.components.swing;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BlankKnob extends JComponent implements MouseListener, MouseMotionListener {

    protected boolean isLinearTransformed;
    protected float shift;
    protected float factor;

    /**
     * Layout class for a BlankKnob
     * Note that for negative values a linear transformation is applied to the values to only contain non-negative values.
     * Internally this needs to be like this since calculating the angle of the knob is not robust to negative values
     * Internally you MUST directly use {@link #value} since its the transformed domain, externally you can only use
     * {@link #getValue()} / {@link #setValue(float)} this transforms / invertes the transformation to return the original values
     * This gets applied at construction by default
     */
    private class BlankKnobUI extends ComponentUI {

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

        protected int textStart;
        protected int lowerStart;

        public BlankKnobUI(BlankKnob b){
            background = Color.WHITE;
            this.knob = Color.BLACK;

            this.strokeCircle = b.size.radius / 4;
            this.strokeIndicator = (int)Math.sqrt(this.strokeCircle * 2);
            this.theta = theta(b);
            Font font = new Font("Fira Mono", Font.BOLD, b.size.fontSize);
            Font fontLower = new Font("Fira Mono", Font.BOLD, b.size.fontSize-2);
            FontMetrics metrics = getFontMetrics(font);
            FontMetrics lowerMetrics = getFontMetrics(fontLower);
            int textHeightLower = lowerMetrics.getHeight();
            int textHeight = metrics.getHeight();
            int paddingText = 4;
            this.dimensionWidth = 2*b.size.radius + 2*b.size.offset;
            this.dimensionHeight = 2*b.size.radius + 2*b.size.offset + textHeight + paddingText + textHeightLower;
            this.textStart = 2*b.size.radius + 2*b.size.offset;
            this.lowerStart = textStart + textHeight + paddingText;
            setPreferredSize(new Dimension(dimensionWidth, dimensionHeight));
        }
        /**
         * Draws the Knob
         * @param g graphics canvas
         * @param c instance of a BlankKnob, due to abstraction as a superclass
         */
        public void paint(Graphics g, JComponent c) throws UnsupportedOperationException {
            if(!(c instanceof BlankKnob)){
                throw new UnsupportedOperationException();
            }
            BlankKnob b = (BlankKnob) c;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle r = g2d.getClipBounds();
            //g.setClip(r.x, r.y, dimensionWidth, dimensionHeight);
            // Draw the knob backdrop
            g.setColor(this.knob);
            g2d.setStroke(new BasicStroke(strokeCircle));
            g.drawOval(b.size.offset,b.size.offset,2*b.size.radius,2*b.size.radius);
            g.setColor(this.background);
            g2d.setStroke(new BasicStroke(strokeIndicator));
            // get the point on the outer knob circle
            this.theta = theta(b);
            Point pt = getKnobAngle(b);
            int xc = (int)pt.getX();
            int yc = (int)pt.getY();
            // Draw the knob state indicator.
            g.setColor(knob);
            g2d.drawLine(xc, yc, b.size.radius + b.size.offset, b.size.radius + b.size.offset);
            paintLabel(g2d, b);

            if(b.pressed){
                Font font = new Font("Fira Mono", Font.BOLD, b.size.fontSize - 2);
                FontMetrics metrics = g.getFontMetrics(font);
                String s = ((Math.round((float)b.getValue() * 1000) / 1000f) + "");
                s = s.substring(0, Math.min(s.length(), 6));
                int textWidth = metrics.stringWidth(s);
                int textHeight = metrics.getHeight();
                int width = 2*b.size.offset + 2*b.size.radius;
                int x = r.x + (width - textWidth) / 2;
                g.setColor(Color.BLACK);
                g.fillRoundRect(r.x, lowerStart - textHeight / 2 - 1, r.width, textHeight, 5, 5);
                g.setColor(Color.WHITE);
                g.setFont(font);

                g.drawString(s, x, lowerStart + 2);
            }
        }

        /**
         * Calculates theta by multiplying the value/maxValue ratio with a full rotation
         * @return rotational angle in rad
         */
        private double theta(BlankKnob b){
            // calculates theta by multiplying the value/maxValue ratio with a quarter rotation
            return b.value/b.maxValue * 0.5 * Math.PI;
        }

        /**
         * Paints a label onto a component
         * @param g graphics canvas
         * @param b knob component
         */
        private void paintLabel(Graphics2D g, BlankKnob b){
            Rectangle rect = g.getClipBounds();
            Font font = new Font("Fira Mono", Font.BOLD, b.size.fontSize);
            FontMetrics metrics = g.getFontMetrics(font);
            int textWidth = metrics.stringWidth(b.label);
            int textHeight = metrics.getHeight();
            int width = rect.width;
            int x = rect.x + (width - textWidth) / 2;
            int y = textStart + textHeight / 2;
            g.setFont(font);
            g.drawString(b.label, x, y);
        }

        /**
         * Calculates the point on the knob circle to where to draw the indicator line to
         * @return Point on the knob circle
         */
        private Point getKnobAngle(BlankKnob b) {
            // basically tells the internal state theta to be rotated by 5/4 clockwise (could be the same as 3/4 ccw)
            double h = 3 * (theta) + 5.0/4.0 * Math.PI;
            // calculates x and y with basic trigonometric functions
            int xcp = (int)(b.size.radius * Math.sin(h));
            int ycp = (int)(b.size.radius * Math.cos(h));
            // calculate the this.size.offset from knob center
            int xc = b.size.radius + b.size.offset + xcp;
            int yc = b.size.radius + b.size.offset - ycp;
            // Create the new Point
            return new Point(xc,yc);
        }

        /**
         * Function for setting the line thickness for the layout class according to MVC
         * @param thickness thickness as integer
         */
        protected void setStrokeThickness(int thickness){
            this.strokeCircle = thickness;
            this.strokeIndicator = thickness / 2;
        }
    }

    /**
     * Constant holding the knob size parameters
     */

    public static class Size {
        protected final int radius;
        protected final int offset;
        protected final int fontSize;

        public Size(int radius, int offset){
            this(radius, offset, (int)(10 + Math.pow(2, Math.log(radius/12)/Math.log(2))));
        }

        public Size(int radius, int offset, int fontSize){
            this.radius = radius;
            // this.size.offset shall not be bigger than this.size.radius
            this.offset = Math.min(offset, this.radius);
            this.fontSize = fontSize;
        }
        protected int radius(){
            return this.radius;
        }
        protected int offset(){
            return this.offset;
        }
    }

    public static final Size SMALL = new Size(12, 6, 10);

    public static final Size MEDIUM = new Size(24, 8, 12);

    public static final Size LARGE = new Size(48, 12, 14);

    public static class Parameters {
        protected float min;
        protected float max;
        protected float scrollFactor;
        protected boolean snapToTicks;
        protected boolean isLinearScale;
        protected float scalingFactor;
        public Parameters(float min, float max, float scrollFactor, boolean snapToTicks, boolean isLinearScale){
            this(min, max, scrollFactor, snapToTicks, isLinearScale, 1);
        }
        public Parameters(float min, float max, float scrollFactor, boolean snapToTicks, boolean isLinearScale, double scalingFactor){
            this(min, max, scrollFactor, snapToTicks, isLinearScale, (float)scalingFactor);
        }
        public Parameters(float min, float max, float scrollFactor, boolean snapToTicks, boolean isLinearScale, float scalingFactor){
            this.min = min;
            this.max = max;
            this.scrollFactor = scrollFactor;
            this.isLinearScale = isLinearScale;
            this.snapToTicks = snapToTicks;
            this.scalingFactor = scalingFactor;
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
     * Creates a
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

        this.minValue = this.params.min;
        this.maxValue = this.params.max;

        if(value <= maxValue && value >= minValue){
            this.value = value;
        } else {
            value = 0;
        }

        this.shift = 0;
        this.factor = 1;
        this.isLinearTransformed = false;
        // TODO since calculating theta like the current implementation does not handle negative values well, we initially transform the domain to non-negative values
        if(this.minValue < 0 || this.maxValue < 0){
            // since value is in the domain as assured before
            this.isLinearTransformed = true;
            this.shift = Math.abs(this.minValue);
            this.setValue(this.value);
            this.setMaxValue(this.maxValue);
            this.setMinValue(this.minValue);
        }

        this.label = label;
        this.scrollFactor = (params.scrollFactor > 0 ? params.scrollFactor : 1);
        // add default listeners to the knob
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setUI(new BlankKnobUI(this));
    }

    /**
     * Set the stroke thickness of the outer circle
     * By design, the indicator stroke thickness is less than that, floor(thickness/2)
     * @param thickness width of the line of the outer circle
     */
    public void setStrokeThickness(int thickness) {
        ((BlankKnobUI)this.getUI()).setStrokeThickness(thickness);
    }

    /**
     * Return the minimum size that the knob would like to be.
     * @return the minimum size of the JKnob.
     */
    public Dimension getMinimumSize() {
        return new Dimension(((BlankKnobUI)this.getUI()).dimensionWidth, ((BlankKnobUI)this.getUI()).dimensionHeight);
    }

    /**
     * Return the preferred size that the knob would like to be.
     * @return the minimum size of the JKnob.
     */
    public Dimension getPreferredSize() {
        return new Dimension(((BlankKnobUI)this.getUI()).dimensionWidth, ((BlankKnobUI)this.getUI()).dimensionHeight);
    }

    /**
     * Gets the current value of the knob
     * @return the current value of the knob
     */
    public float getValue(){
        return ((this.value - this.shift) / this.factor);
    }

    /**
     * Returns the Parameters object containing most value boundary context for the knob
     * @return Parameters object
     */
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
     * NOTE The fired PropertyChangeListener contains the untransformed values!
     * Also repaints knob on change
     * @param value new current value of the knob
     */
    public void setValue(float value){
        double temp = this.getValue();
        float tValue = this.factor * value + this.shift;
        if(tValue <= maxValue && tValue >= minValue){
            this.value = tValue;
            repaint();
            valueChange.firePropertyChange("value", temp, value);
        }
    }

    /**
     * Sets the value untransformed
     * NOTE only for internal usage
     * @param value new value
     */
    protected void _setValue(float value){
        double temp = this.getValue();
        if(value <= maxValue && value >= minValue){
            this.value = value;
            repaint();
            valueChange.firePropertyChange("value", temp, value);
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
        this.minValue = this.factor * minValue + this.shift;
        this.params.min = this.factor * minValue + this.shift;;
        repaint();
    }

    /**
     * Sets the maximum value of the knob
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = this.factor * maxValue + this.shift;;
        this.params.max = this.factor * maxValue + this.shift;;
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
        this.mousePt = e.getPoint();
        this.pressed = this.isOnSpot();
    }

    /**
     * When the button is released, the dragging of the spot
     * is disabled.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse release.
     */
    public void mouseReleased(MouseEvent e) {
        this.pressed = false;
        repaint();
        this.prev = this.mousePt.y;
    }

    // Methods from the MouseMotionListener interface.

    private void updateValue(MouseEvent e){
        if(this.pressed){
            float dy = (prev - e.getY()) * this.scrollFactor;
            // check for the value to be in range of minValue and maxValue
            if(this.value + dy <= maxValue && this.value + dy >= minValue){
                this._setValue(this.value + dy);
            } else if (this.value + dy > maxValue){
                this._setValue(maxValue);
            } else if (this.value + dy < minValue){
                this._setValue(minValue);
            }
            if(this.snapToTicks){
                this._setValue((int)this.value);
            }
            // update previous state variable
            prev = e.getY();
            // repaint the whole knob
            repaint();
        }
    }

    /**
     * Empty method because nothing happens when the mouse
     * is moved if it is not being dragged.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse move.
     */
    public void mouseMoved(MouseEvent e) {
        this.updateValue(e);
    }

    /**
     * Compute the new angle for the spot and repaint the
     * knob.  The new angle is computed based on the new
     * mouse position.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse drag.
     */
    public void mouseDragged(MouseEvent e) {
        this.updateValue(e);
    }

    /**
     * Determine if the mouse click was on the spot or
     * not. If it was return true, otherwise return
     * false.
     *
     * @return true if x,y is on the spot and false if not.
     */
    private boolean isOnSpot() {
        return (this.mousePt.distance(new Point(this.size.radius + this.size.offset, this.size.radius + this.size.offset)) < this.size.radius);
    }
}
