/*
 * This file is part of Beads. See http://www.beadsproject.net for all information.
 */
package synth.filter.models;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.DataBead;
import net.beadsproject.beads.data.DataBeadReceiver;
import net.beadsproject.beads.ugens.Static;

/**
 * A simple implementation of a multi-channel biquad filter. It calculates
 * coefficients based on three parameters (frequency, Q, and gain - the latter
 * only relevant for EQ and shelving filter), each of which may be specified by
 * a static float or by the output of a UGen.
 * <p>
 * Filter parameters may be set with individual setter functions (
 * {@link #setFrequency(float) setFreq}, {@link #setQ(float) setQ}, and
 * {@link #setGain(float) setStaticGain}), or by passing a DataBead with the
 * appropriate properties to {@link #setParams(DataBead) setParams}. (Messaging
 * the filter with a DataBead is equivalent to calling setParams.) Setter
 * methods return the instance, so they may be strung together:
 * <p>
 * <code>filt.setFreq(200).setQ(30).setStaticGain(.4);</code>
 * <p>
 *
 * @beads.category filter
 * @author Benito Crawford
 * @version 0.9.6
 */
public class BiquadFilter extends FilterModel implements DataBeadReceiver {

  	protected float bo1 = 0, bo2 = 0, bi1 = 0, bi2 = 0;
 	protected float[] bo1m, bo2m, bi1m, bi2m;

	protected int channels = 1;
	protected Mode mode = null;

	// for analysis
	protected double w = 0, ampResponse = 0, phaseResponse = 0, phaseDelay = 0;
	protected double frReal = 0, frImag = 0;

	protected ValCalculator vc;

	/**
	 * Constructor for a multi-channel low-pass biquad filter UGen with the
	 * specified number of channels.
	 * @param context The audio context.
	 * @param channels The number of channels.
	 */
	public BiquadFilter(AudioContext context, int channels) {
		this(context, Mode.LPF);
	}

	/**
	 * Constructor for a multi-channel biquad filter UGen of specified type with
	 * the specified number of channels. See {@link #setMode(Mode) setMode} for a
	 * list of supported filter modes.
	 *
	 * @param context The AudioContext.
	 * @param mode
	 */
	public BiquadFilter(AudioContext context, Mode mode) {
		super(context);
		this.channels = super.getOuts();
        bi1m = new float[this.channels];
        bi2m = new float[this.channels];
        bo1m = new float[this.channels];
        bo2m = new float[this.channels];
		setMode(mode);
		this.setFrequency(100).setQ(1).setGain(0);
		this.outputInitializationRegime = OutputInitializationRegime.ZERO;
	}

	public BiquadFilter(AudioContext ac, Mode mode, UGen frequency, UGen q, UGen gain){
	    this(ac, mode);
	    this.setFrequency(frequency).setQ(q).setGain(gain);
    }

    public BiquadFilter(AudioContext ac, Mode mode, float frequency, float q, float gain){
	    this(ac, mode, new Static(ac, frequency), new Static(ac, q), new Static(ac, gain));
    }

	public BiquadFilter setMode(Mode mode){
		if(mode != null){
			switch(mode){
				case LPF :
					vc = new LPFValCalculator();
					break;
                case HPF :
                    vc = new HPFValCalculator();
                    break;
				default: break;
			}
		}
		return this;
	}

    /**
     * Sets the specific UGen input buffer to bypass addInput()
     * @param in input buffer
     * @param channel input channel
     */
	public void setInputBuffer(float[] in, int channel){
	    this.bufIn[channel] = in;
    }

	@Override
	public void calculateBuffer() {
		float[] bi, bo;
        // multi-channel version
        frequency.update();
        q.update();
        gain.update();
        tfreq = frequency.getValue(0, 0);
        tq = q.getValue(0, 0);
        tgain = gain.getValue(0, 0);
        vc.calcVals();
        for (int i = 0; i < channels; i++) {
            bufOut[i][0] = (b0 * bufIn[i][0] + b1 * bi1m[i] + b2 * bi2m[i] - a1 * bo1m[i] - a2 * bo2m[i]) / a0;
        }
        tfreq = frequency.getValue(0, 1);
        tq = q.getValue(0, 1);
        tgain = gain.getValue(0, 1);
        vc.calcVals();
        for (int i = 0; i < channels; i++) {
            bufOut[i][1] = (b0 * bufIn[i][1] + b1 * bufIn[i][0] + b2 * bi1m[i] - a1 * bufOut[i][0] - a2 * bo1m[i]) / a0;
        }

        // main loop
        for (int j = 2; j < bufferSize; j++) {
            tfreq = frequency.getValue(0, j);
            tq = q.getValue(0, j);
            tgain = gain.getValue(0, j);
            vc.calcVals();
            for (int i = 0; i < channels; i++) {
                bufOut[i][j] = (b0 * bufIn[i][j] + b1 * bufIn[i][j - 1] + b2 * bufIn[i][j - 2] - a1 * bufOut[i][j - 1] - a2 * bufOut[i][j - 2]) / a0;
            }

        }
        for (int i = 0; i < channels; i++) {
            // get 2 samples of "memory" between sample vectors
            bi2m[i] = bufIn[i][bufferSize - 2];
            bi1m[i] = bufIn[i][bufferSize - 1];
            bo2m[i] = bufOut[i][bufferSize - 2];

            // and check to make sure filter didn't blow up
            if (Float.isNaN(bo1m[i] = bufOut[i][bufferSize - 1]))
                reset();
        }
	}

	/**
	 * Resets the filter in case it "explodes".
	 */
	public void reset() {
		for (int i = 0; i < channels; i++) {
			bi1m[i] = 0;
			bi2m[i] = 0;
			bo1m[i] = 0;
			bo2m[i] = 0;
		}
		bi1 = 0;
		bi2 = 0;
		bo1 = 0;
		bo2 = 0;
	}

	private class LPFValCalculator extends ValCalculator {
	    public void calcVals() {
            float omega = two_pi_over_sf * tfreq;
            float sn = (float)Math.sin(omega);
            float cs = (float)Math.cos(omega);
            float alpha = sn / (2.f * tq);
            b0 = (1.f - cs) / 2.f;
            b1 = 1.f - cs;
            b2 = (1.f - cs) / 2.f;
            a0 = 1.f + alpha;
            a1 = -2.f * cs;
            a2 = 1.f - alpha;
        }
    }

    private class HPFValCalculator extends ValCalculator {
        public void calcVals() {
            float omega = two_pi_over_sf * tfreq;
            float sn = (float)Math.sin(omega);
            float cs = (float)Math.cos(omega);
            float alpha = sn / (2.f * tq);
            b0 = (1.f + cs) / 2.f;
            b1 = -(1.f + cs);
            b2 = (1.f + cs) / 2.f;
            a0 = 1.f + alpha;
            a1 = -2.f * cs;
            a2 = 1.f - alpha;
        }
    }

	private class BPFValCalculator extends ValCalculator {
		public void calcVals() {
			float w = two_pi_over_sf * tfreq;
			b1 = 0;
			b2 = 0 - (b0 = (float) Math.sin(w) / tq * .5f);
			a0 = 1 + b0;
			a1 = -2 * (float) Math.cos(w);
			a2 = 1 - b0;
		}
	}

	/**
	 * Sets the filter parameters with a DataBead.
	 * <p>
	 * Use the following properties to specify filter parameters:
	 * </p>
	 * <ul>
	 * <li>"filterType": (int) The filter type.</li>
	 * <li>"frequency": (float or UGen) The filter frequency.</li>
	 * <li>"q": (float or UGen) The filter Q-value.</li>
	 * <li>"gain": (float or UGen) The filter gain.</li>
	 * </ul>
	 *
	 * @param paramBead The DataBead specifying parameters.
	 * @return This filter instance.
	 */
	public BiquadFilter setParams(DataBead paramBead) {
		if (paramBead != null) {
			Object o;

			o = paramBead.get("type");
			if (o instanceof Type) {
                setType((Mode) o);
			} else {
				setType(Mode.LPF);
			}

			if ((o = paramBead.get("frequency")) != null) {
				if (o instanceof UGen) {
					setFrequency((UGen) o);
				} else {
					setFrequency(paramBead.getFloat("frequency", tfreq));
				}
			}

			if ((o = paramBead.get("q")) != null) {
				if (o instanceof UGen) {
					setQ((UGen) o);
				} else {
					setQ(paramBead.getFloat("q", tq));
				}
			}

			if ((o = paramBead.get("gain")) != null) {
				if (o instanceof UGen) {
					setGain((UGen) o);
				} else {
					setGain(paramBead.getFloat("gain", tgain));
				}
			}
		}
		return this;
	}

	public void messageReceived(Bead message) {
		if (message instanceof DataBead) {
			setParams((DataBead) message);
		}
	}

	/**
	 * Equivalent to {@link #setParams(DataBead)}.
	 *
	 * @return This filter instance.
	 */
	public DataBeadReceiver sendData(DataBead db) {
		setParams(db);
		return this;
	}

	/**
	 * Gets a DataBead with the filter's parameters (whether float or UGen),
	 * stored in the keys "frequency", "q", "gain", and "filterType".
	 *
	 * @return The DataBead with stored parameters.
	 */
	public DataBead getParams() {
		DataBead db = new DataBead();
        db.put("frequency", frequency);
        db.put("q", q);
        db.put("gain", gain);
		db.put("mode", mode);
		return db;
	}

	/**
	 * Gets a DataBead with properties "frequency", "q", and "gain" set to their
	 * current float values and "type" set appropriately.
	 *
	 * @return The DataBead with static float parameter values.
	 */
	public DataBead getStaticParams() {
		DataBead db = new DataBead();
		db.put("frequency", frequency);
		db.put("q", q);
		db.put("gain", gain);
		db.put("mode", mode);
		return db;
	}

	/**
	 * Sets the type of filter.
	 * @param nmode The type of filter.
	 */
	public BiquadFilter setType(Mode nmode) {
		if (nmode != mode || vc == null) {
			Mode m = mode;
			mode = nmode;
			switch (mode) {
                case LPF:
                    vc = new LPFValCalculator();
                    break;
                case HPF:
                    vc = new HPFValCalculator();
                    break;
                case BPF:
                    vc = new BPFValCalculator();
                    break;
                default:
					mode = m;
                    break;
			}
			vc.calcVals();
		}
		return this;
	}

	/**
	 * Gets the type of the filter.
	 * @return The filter type.
	 * @see #setMode(Mode)
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Gets an array of the current filter coefficients: {a0, a1, a2, b0, b1,
	 * b2}.
	 * @return The coefficient array.
	 */
	public float[] getCoefficients() {
		return new float[] { a0, a1, a2, b0, b1, b2 };
	}

	/**
	 * Gets an array filled with the filter response characteristics: {frequency
	 * response (real), frequency response (imaginary), amplitude response,
	 * phase response, phase delay, group delay}.
	 * @param freq The frequency to test.
	 * @return The array.
	 */
	public IIRFilterAnalysis getFilterResponse(float freq) {
		return calculateFilterResponse(new float[] { b0, b1, b2 }, new float[] {a0, a1, a2 }, freq, samplingfreq);
	}
}
