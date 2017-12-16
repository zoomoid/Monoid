package synth.filter;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.ugens.IIRFilter;
import net.beadsproject.beads.ugens.IIRFilter.IIRFilterAnalysis;
import java.lang.Math;

public class ButterworthBessel extends IIRFilter {
    /**
     * constants, determining which type of Butterworth filter ist calculated
     */
    public static final int LOWPASS_FIRST_ORDER = 1;
    public static final int HIGHPASS_FIRST_ORDER = 2;
    public static final int LOWPASS_SECOND_ORDER = 3;
    public static final int HIGHPASS_SECOND_ORDER = 4;
    public static final int BANDPASS_SECOND_ORDER = 5;
    public static final int PARAMETRIC_EQ_SECOND_ORDER = 6;

    public static int currType = 1; // inits filter as type Lowpass first order

    private float cutoff;
    private int sampleRate;
    private float q; //quality factor
    private float gain; //for parametricEQ

    private int order = 1;

    /**
     * parameters for IIR filter as needed in analysis method or calculateBuffer
     */
    private float[][] params = new float[2][3];
    private float[] as = new float[3];
    private float[] bs = new float[3];

    public IIRFilter.IIRFilterAnalysis analysis;
    public IIRFilter.IIRFilterAnalysis response;


    /**
     * basic constructor
     * @param ac given Audio Context
     * @param ins input channels
     * @param outs output channels
     */
    public ButterworthBessel(AudioContext ac, int ins, int outs) {
        super(ac, ins, outs);
        analysis = null;
        response = null;
    }

    /**
     * Constructor for Butterworth filters of first order (doesn't need q and g)
     * @param ac given Audio Context
     * @param ins input channels
     * @param outs output channels
     * @param cutoff cutoff frequency
     * @param sampleRate current sample rate, later constant sample rate
     * @param type filter type
     */
    public ButterworthBessel(AudioContext ac, int ins, int outs, float cutoff, int sampleRate, int type) {
        super(ac, ins, outs);
        this.cutoff = cutoff;
        this.sampleRate = sampleRate;
        if(type == 1 || type == 2) {
            this.params = calculateFilterCoefficients(type);
        }
        response = calculateFilterResponse(setAndgetAs(type), setAndgetBs(type), this.cutoff, this.sampleRate);
        analysis = analyzeFilter(as, bs, this.cutoff, this.sampleRate);
    }

    /**
     * Constructor for Butterworth filters except of parametric EQ
     * @param ac given Audio Context
     * @param ins input channels
     * @param outs output channels
     * @param cutoff cutoff frequency
     * @param sampleRate current sample rate, later constant sample rate
     * @param q quality factor
     * @param type filter type
     */
    public ButterworthBessel(AudioContext ac, int ins, int outs, float cutoff, int sampleRate, float q, int type) {
        super(ac, ins, outs);
        this.cutoff = cutoff;
        this.sampleRate = sampleRate;
        this.q = q;
        if(1<= type && 5 >= type) {
            this.params = calculateFilterCoefficients(type);
        }
        response = calculateFilterResponse(setAndgetAs(type), setAndgetBs(type), this.cutoff, this.sampleRate);
        analysis = analyzeFilter(as, bs, this.cutoff, this.sampleRate);
    }

    /**
     * Constructor for Butterworth filters
     * @param ac given Audio Context
     * @param ins input channels
     * @param outs output channels
     * @param cutoff cutoff frequency
     * @param sampleRate current sample rate, later constant sample rate
     * @param q quality factor
     * @param gain Gain for parametric EQ
     * @param type filter type
     */
    public ButterworthBessel(AudioContext ac, int ins, int outs, float cutoff, int sampleRate, float q, float gain,
                             int type) {
        super(ac, ins, outs);
        this.cutoff = cutoff;
        this.sampleRate = sampleRate;
        this.q = q;
        this.gain = gain;
        this.params = calculateFilterCoefficients(type);
        response = calculateFilterResponse(setAndgetAs(type), setAndgetBs(type), this.cutoff, this.sampleRate);
        analysis = analyzeFilter(as, bs, this.cutoff, this.sampleRate);
    }

    public void calculateBuffer() {
        float usedOuts, usedIns;
        bufOut[0][0] = bs[0] * bufIn[0][0];
        bufOut[0][1] = bs[0] * bufIn[0][1] - as[1] * bufOut[0][0];
        for(int i = 2; i < bufferSize; i++) {
            /*
            if(as[0] == 0) {
                bufOut[0][i] = bs[0] * bufIn[0][i] + bs[1] * bufIn[0][i - 1] + bs[2] * bufIn[0][i - 2] - as[1] *
                        bufOut[0][i - 1] - as[2] * bufOut[0][i - 2];
            } else {
                bufOut[0][i] = (bs[0] * bufIn[0][i] + bs[1] * bufIn[0][i - 1] + bs[2] * bufIn[0][i - 2] - as[1] *
                        bufOut[0][i - 1] - as[2] * bufOut[0][i - 2]) / as[0];
            }
            */
            if(i - this.order >= 0) {
                usedOuts = 0f;
                usedIns = 0f;
                for(int j = this.order; j >= 0; j--) {
                    int coeff = this.order - j;
                    int index = i-j;
                    usedIns += (bufIn[0][index] * bs[coeff]);
                    if(j > 0) {
                        usedOuts += (bufOut[0][index] * as[coeff]);
                    }
                }
                bufOut[0][i] = usedIns-usedOuts;
            } else {
                bufOut[0][i] = 0f;
            }
        }
    }

    /**
     * Calls the calculating method, returning Array of coefficients for IIR Filter
     * @param type Filter type
     * @return Array of coefficients for IIR Filter (row 0 = a, row 1 = b)
     */
    public float[][] calculateFilterCoefficients(int type) {
        switch(type) {
            case LOWPASS_FIRST_ORDER:
                currType = LOWPASS_FIRST_ORDER;
                order = 1;
                return calculateLowpassFirstOrder(this.cutoff, this.sampleRate);
            case HIGHPASS_FIRST_ORDER:
                order = 1;
                currType = HIGHPASS_FIRST_ORDER;
                return calculateHighpassFirstOrder(this.cutoff, this.sampleRate);
            case LOWPASS_SECOND_ORDER:
                order = 2;
                currType = LOWPASS_SECOND_ORDER;
                return calculateLowpassSecondOrder(this.cutoff, this.sampleRate, 0.71f);
            case HIGHPASS_SECOND_ORDER:
                order = 2;
                currType = HIGHPASS_SECOND_ORDER;
                return calculateHighpassSecondOrder(this.cutoff, this.sampleRate, 0.71f);
            case BANDPASS_SECOND_ORDER:
                order = 2;
                currType = BANDPASS_SECOND_ORDER;
                return calculateBandpassSecondOrder(this.cutoff, this.sampleRate, 0.71f);
            case PARAMETRIC_EQ_SECOND_ORDER:
                order = 2;
                currType = PARAMETRIC_EQ_SECOND_ORDER;
                return calculateParametricEQSecondsOrder(this.cutoff, this.sampleRate, 0.71f, this.gain);
            default:
                    break;
        }
        return null;
    }

    /**
     * Calculates IIR Parameter for lowpass of first order filter
     * @param cutoff cutoff frequency
     * @param sampleRate sample rate
     * @return Parameters for IIR filter
     */
    public float[][] calculateLowpassFirstOrder(float cutoff, float sampleRate) {
        float[][] output = new float[2][3];

        double w = Math.tan(Math.PI * (cutoff/sampleRate));
        double n = 1/(1+w);
        output[1][0] = (float) (w*n); //b0
        output[1][1] = output[1][0]; //b1
        output[0][1] = (float) (n*(w-1)); //a1
        return output;
    }

    /**
     * Calculates IIR paramters for highpast first order filter
     * @param cutoff cutoff frequency
     * @param sampleRate sample rate
     * @return coefficients for IIR filter
     */
    public float[][] calculateHighpassFirstOrder(float cutoff, float sampleRate) {
        float[][] output = new float[2][3];
        double w = Math.tan(Math.PI * (cutoff/sampleRate));
        double n = 1/(1+w);
        output[1][0] = (float) n; //b0
        output[1][1] = -(output[1][0]); //b1
        output[0][1] = (float) (n*w-1); //a1

        return output;
    }

    /**
     * Calculates IIR parameters for lowpass second order filter
     * @param cutoff cutoff frequency
     * @param sampleRate sample rate
     * @param q quality factor
     * @return coefficients for IIR filter
     */
    public float[][] calculateLowpassSecondOrder(float cutoff, float sampleRate, float q) {
        float[][] output = new float[2][3];
        double w = Math.tan(Math.PI * (cutoff/sampleRate));
        double n = 1/((w*w) + (w/q) + 1);
        output[1][0] = (float) (n * w * w); //b0
        output[1][1] = 2 * (output[1][0]); //b1
        output[1][2] = output[1][0]; //b2
        output[0][1] = (float) (2 * n * (w * w - 1)); //a1
        output[0][2] = (float) (n * (w * w - w/q + 1)); //a2

        return output;
    }

    /**
     * Calculates IIR parameters for highpass second order filter
     * @param cutoff cutoff frequency
     * @param sampleRate sample rate
     * @param q quality factor
     * @return coefficients for IIR filter
     */
    public float[][] calculateHighpassSecondOrder(float cutoff, float sampleRate, float q) {
        float[][] output = new float[2][3];
        double w = Math.tan(Math.PI * (cutoff/sampleRate));
        double n = 1/((w*w) + (w/q) + 1);
        output[1][0] = (float) (n); //b0
        output[1][1] = (float) (-2 * n); //b1
        output[1][2] = output[1][0]; //b2
        output[0][1] = (float) (2 * n * (w * w - 1)); //a1
        output[0][2] = (float) (n * (w * w - w/q + 1)); //a2

        return output;
    }

    /**
     * Calculates IIR parameters for bandpass seconds order filter
     * @param cutoff cutoff frequency
     * @param sampleRate sample rate
     * @param q quality factor
     * @return coefficients for IIR filter
     */
    public float[][] calculateBandpassSecondOrder(float cutoff, float sampleRate, float q) {
        float[][] output = new float[2][3];
        double w = Math.tan(Math.PI * (cutoff/sampleRate));
        double n = 1/((w*w) + (w/q) + 1);
        output[1][0] = (float) (n * w/q); //b0
        output[1][1] = 0.0f; //b1
        output[1][2] = output[1][0] * (-1); //b2
        output[0][1] = (float) (2 * n * (w * w - 1)); //a1
        output[0][2] = (float) (n * (w * w - w/q + 1)); //a2

        return output;
    }

    /**
     * Calculates IIR parameters for parametric EQ second order filter
     * @param cutoff cutoff frequency
     * @param sampleRate sample rate
     * @param q quality factor
     * @param gain gain
     * @return coefficients for IIR filter
     */
    public float[][] calculateParametricEQSecondsOrder(float cutoff, float sampleRate, float q, float gain) {
        float[][] output = new float[2][3];
        if(gain < 1) {
            q = q*gain;
        }
        float a = (float) (gain/q);
        double w = Math.tan(Math.PI * (cutoff/sampleRate));
        double n = 1/((w*w) + (w/q) + 1);
        output[1][0] = (float) (n * (w * w + w * a + 1)); //b0
        output[1][1] = (float) (2 * n * (w * w - 1)); //b1
        output[1][2] = (float) (n * (w * w - w * a + 1)); //b2
        output[0][1] = output[1][1]; //a1
        output[0][2] = (float) (n * (w * w - w / q + 1)); //a2

        return output;
    }

    public void printCoefficients(float[][] coeff) {
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 3; j++) {
                System.out.println(coeff[i][j] + "\t ");
            }
        }
    }

    /**
     * Get the a IIR filter coefficients
     * @param type filter type
     * @return array of parameters A of IIR Filter
     */
    public float[] setAndgetAs(int type) {
        for(int i = 0; i <= 2; i++) {
            as[i] = params[0][i];
        }
        return as;
    }

    /**
     * Get the b IIR filter coefficients
     * @param type filter type
     * @return array of parameters B of IIR Filter
     */
    public float[] setAndgetBs(int type) {
        for(int i = 0; i <= 2; i++) {
            bs[i] = params[1][i];
        }
        return bs;
    }

    public IIRFilter.IIRFilterAnalysis getFilterResponse(float freq) {
        return this.response;
    }

    /*------------------------------------------------------------------------------------------------------------------
     * casual getters and setters
     *----------------------------------------------------------------------------------------------------------------*/
    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public void setQ(float q) {
        this.q = q;
    }

    public float getCutoff() {
        return cutoff;
    }

    public float getQ() {
        return q;
    }

    public float getGain() {
        return gain;
    }

    public int getSampleRate() {
        return sampleRate;
    }

}
