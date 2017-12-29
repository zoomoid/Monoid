package synth.ui;

import synth.container.Device;
import synth.filter.Filter;
import synth.osc.Oscillator;

public class ModulationUI {

    public ModulationUI(SynthesizerUserInterfaceModule ui){
        if(ui instanceof OscillatorUI){
            Oscillator osc = ((OscillatorUI)ui).getAssociatedDevice();
            // TODO create both LFO and Envelope controls for frequency and gain, which shall primarily be the modulatable parameters
            
        } else if(ui instanceof FilterUI){
            Filter filter = ((FilterUI)ui).getAssociatedDevice();
            // TODO create both LFO and Envelope controls for cutoff, resonance and gain
        } else {
            Device d = ui.getAssociatedDevice();
            // TODO display something like "No modulation available for this device"
            // No modulation allowed for this module
        }
    }
}
