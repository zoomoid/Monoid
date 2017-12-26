package synth.osc;

public interface UnisonOscillator {

    int voices = 0;

    float spread = 0f;

    float blend = 0f;

    float spreadFunction = 0f;

    float getBlend();

    int getVoices();

    float getSpread();

    float getSpreadFunction();

    //MultivoiceOscillator getUnison();

    UnisonOscillator setBlend(float blend);

    UnisonOscillator setSpread(float spread);

    UnisonOscillator setVoices(int voices);

    UnisonOscillator setSpreadFunction(float spreadFunction);
}
