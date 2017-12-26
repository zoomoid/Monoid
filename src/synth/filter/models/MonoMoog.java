package synth.filter.models;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Static;

public class MonoMoog extends FilterModel {

    protected AudioContext context;

    private UGen resonance;
    private float tfreq, tq, tgain, tres;

    float f, p, ql;

    public MonoMoog(AudioContext ac, Type type, float frequency, float q, float gain){
        this(ac, type, new Static(ac, frequency), new Static(ac, q), new Static(ac, gain));
    }

    public MonoMoog(AudioContext ac, Type fType, UGen frequency, UGen q, UGen gain){
        super(ac);
        this.context = ac;
        this.resonance = new Static(this.context, 1f);
        this.setType(Type.LPF).setFrequency(frequency).setQ(q).setGain(gain);
    }

    // TODO anything else than LPF is currently not supported
    public MonoMoog setType(Type type){
        if(type != null){
            switch(type){
                case LPF :
                    vc = new LPFValCalculator();
                    break;
                /*case HPF :
                    vc = new HPFValCalculator();
                    break;*/
                default: break;
            }
        }
        return this;
    }

    public MonoMoog setResonance(UGen resonance){
        if(resonance != null){
            this.resonance = resonance;
            this.tres = resonance.getValue();
        }
        vc.calcVals();
        return this;
    }

    public UGen getResonance(){
        return this.resonance;
    }

    public void calculateBuffer(){
        // TODO Implement feedback to filter for MOOG like resonance
        float t1 = 0, t2 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0;
        int i = 0;
        for(int j = 0; j < bufferSize; j++){
            this.frequency.update();
            this.gain.update();
            this.q.update();
            this.resonance.update();
            vc.calcVals();
            bufIn[i][j] -= ql * b4;                          //feedback
            t1 = b1;
            b1 = (bufIn[i][j] + b0) * p - b1 * f;
            t2 = b2;
            b2 = (b1 + t1) * p - b2 * f;
            t1 = b3;
            b3 = (b2 + t2) * p - b3 * f;
            b4 = (b3 + t1) * p - b4 * f;
            b4 = b4 - b4 * b4 * b4 * 0.166667f;    //clipping
            b0 = bufIn[i][j];
            bufOut[i][j] = b4;
        }
        for(i = 1; i < outs; i++){
            bufOut[i] = bufOut[0];
        }
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
            ql = 1.0f - tfreq;
            p = tfreq + 0.8f * tfreq * tq;
            f = p + p - 1.0f;
            ql = tres * (1.0f + 0.5f * tq * (1.0f - tq + 5.6f * tq * tq));
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
            ql = 1.0f - tfreq;
            p = tfreq + 0.8f * tfreq * tq;
            f = p + p - 1.0f;
            ql = tres * (1.0f + 0.5f * tq * (1.0f - tq + 5.6f * tq * tq));
        }
    }

    public IIRFilterAnalysis getFilterResponse(float freq) {
        return calculateFilterResponse(new float[] { b0, b1, b2 }, new float[] {
                a0, a1, a2 }, freq, samplingfreq);
    }

}
