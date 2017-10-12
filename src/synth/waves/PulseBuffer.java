package synth.waves;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.BufferFactory;

/**
 * Creates a {@link Buffer} of pulse wave in the range [0,1]
 */
public class PulseBuffer extends BufferFactory {

    /** Pulse Width Ratio */
    private final float pulseWidth;

    /**
     * Instanciates a new pulse buffer
     *
     * @param pulseWidth the pulse width in ratio to buffer size
     */
    public PulseBuffer(int pulseWidth) {
        switch(pulseWidth) {
            case 2 : case 4 : case 8 : this.pulseWidth = (float)(1/pulseWidth); break;
            default: this.pulseWidth = 0.5f; break;
        }
    }

    /* (non-Javadoc)
	 * @see net.beadsproject.beads.data.BufferFactory#generateBuffer(int)
	 */
    public Buffer generateBuffer(int bufferSize) {
        Buffer b = new Buffer(bufferSize);
        int phaseJump = (int)(bufferSize * this.pulseWidth);
        for(int i = 0; i < phaseJump; i++) {
            b.buf[i] = 1f;
        }
        for(int i = phaseJump; i < bufferSize; i++) {
            b.buf[i] = -1f;
        }
        return b;
    }

    /* (non-Javadoc)
     * @see net.beadsproject.beads.data.BufferFactory#getName()
     */
    public String getName() {
        return "Pulse";
    }
}
