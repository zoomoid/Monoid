package synth.modulation;

import net.beadsproject.beads.core.AudioContext;

public class Sum extends Modulator implements Modulatable {

    private Static aStatic;
    private NormEnvelope anEnvelope;
    private NormLFO aLFO;

    public Sum(AudioContext ac, Static pStatic, Envelope envelope, LFO lfo){
        super(ac);
        this.aStatic = pStatic;
        this.anEnvelope = new NormEnvelope(ac, envelope.attack, envelope.decay, envelope.sustain, envelope.release);
        this.aLFO = new NormLFO(ac, lfo.type, lfo.mode, lfo.frequency, lfo.amplitude);
    }

    public void noteOn(){
        this.aStatic.noteOn();
        this.aLFO.noteOn();
        this.anEnvelope.noteOn();
    }
    public void noteOff(){
        this.aStatic.noteOff();
        this.aLFO.noteOff();
        this.anEnvelope.noteOff();
    }

    public void setValue(float value){
        this.aStatic.setValue(value);
        this.aLFO.setValue(value);
        this.anEnvelope.setValue(value);
    }

    public Static getStatic() {
        return aStatic;
    }

    public NormEnvelope getEnvelope() {
        return anEnvelope;
    }

    public NormLFO getLFO() {
        return aLFO;
    }

    public Sum clone(){
        Sum s = new Sum(this.context, this.aStatic, this.anEnvelope, this.aLFO);
        s.setName(this.toString());
        return s;
    }

    @Override
    public void calculateBuffer(){
        aStatic.update();
        aLFO.update();
        anEnvelope.update();
        for(int i = 0; i < bufferSize; i++){
            this.bufOut[0][i] = this.aStatic.getValue(0, i) + 0.5f * this.anEnvelope.getValue(0, i) + 0.5f * this.aLFO.getValue(0, i);
        }
    }

    public float getValue(){
        return this.aStatic.getValue();
    }

    public void setLFO(LFO lfo){
        this.aLFO = lfo.normalize();
    }

    public void setStatic(Static pStatic){
        this.aStatic = pStatic;
    }

    public void setEnvelope(Envelope envelope){
        this.anEnvelope = envelope.normalize();
    }
}
