package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class Normaliser extends UGen {

    protected final float NORMALISATION_FACTOR;

    private final boolean staticNormalisation;

    /**
     * 'Normalises' the input signal by a static factor
     * @param context audio context
     */
    public Normaliser(AudioContext context){
        this(context, true);
    }

    /**
     * Normalises the input signal either by a constant factor or by the current maximum in the input buffer
     * @param context audio context
     * @param useConstantNormalisation true if constant, otherwise false
     */
    public Normaliser(AudioContext context, boolean useConstantNormalisation){
        this(context, useConstantNormalisation, 2);
    }

    public Normaliser(AudioContext context, boolean useConstantNormalisation, float compensation){
        super(context, 2, 2);
        outputInitializationRegime = OutputInitializationRegime.RETAIN;
        this.staticNormalisation = useConstantNormalisation;
        this.NORMALISATION_FACTOR = compensation;
    }

    @Override
    public void calculateBuffer(){
        for(int i = 0; i < outs; i++){
            if(!this.staticNormalisation){
                float normalisationFactor = 1 / this.max(bufIn[i]);
                for(int j = 0; j < bufferSize; j++){
                    bufOut[i][j] = normalisationFactor * bufIn[i][j];
                }
            } else {
                for(int j = 0; j < bufferSize; j++){
                    bufOut[i][j] = NORMALISATION_FACTOR * bufIn[i][j];
                }
            }

        }
    }

    /**
     * Determines the maximum of the current input buffer
     * NOTE that this maximum, since it is used as a scalar is to be strictly non-negative to not invert phase
     * @param input current input buffer
     * @return maximum in that buffer
     */
    private float max(float[] input){
        float max = 0;
        for(int i = 0; i < input.length; i++){
            if(Math.abs(input[i]) > max){
                max = Math.abs(input[i]);
            }
        }
        return max;
    }
}
