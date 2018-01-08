package synth.osc;

public interface UnisonOscillator {

    int MAX_NUM_VOICES = 8;

    float getBlend();

    int getVoices();

    float getSpread();

    float getSpreadFunction();

    UnisonOscillator setBlend(float blend);

    UnisonOscillator setSpread(float spread);

    UnisonOscillator setVoices(int voices);

    UnisonOscillator setSpreadFunction(float spreadFunction);
}
