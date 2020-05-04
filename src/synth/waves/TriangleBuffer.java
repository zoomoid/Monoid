package synth.waves;

import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.BufferFactory;

public class TriangleBuffer extends BufferFactory {
    public TriangleBuffer() {

    }

    public static final Buffer TRIANGLE = (new TriangleBuffer()).getDefault();
    /* (non-Javadoc)
     * @see net.beadsproject.beads.data.BufferFactory#generateBuffer(int)
     */
    public Buffer generateBuffer(int bufferSize) {
        Buffer b = new Buffer(bufferSize);
        for (int i = 0; i < bufferSize; i++){
            float progress = (float) 4*i / (float) bufferSize;
            if (i < bufferSize / 4) {
                // Rise from 0 to 1
                b.buf[i] = 0f + progress;
            } else if (i >= bufferSize / 4 && i < bufferSize / 2) {
                // fall from 1 to 0
                b.buf[i] = 1f - progress;
            } else if (i >= bufferSize / 2 && i < (bufferSize / 4) * 3) {
                b.buf[i] = 0f - progress;
            } else {
                // rise from -1 to 0
                b.buf[i] = -1f + progress;
            }
        }
        return b;
    }


    public Buffer getDefaults(){
        return this.generateBuffer(4096);
    }

    /* (non-Javadoc)
     * @see net.beadsproject.beads.data.BufferFactory#getName()
     */
    public String getName() {
        return "Triangle";
    }
}
