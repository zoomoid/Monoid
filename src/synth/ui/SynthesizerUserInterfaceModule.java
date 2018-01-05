package synth.ui;

import synth.container.Device;

public abstract class SynthesizerUserInterfaceModule {

    Device associatedDevice;

    public Device getAssociatedDevice() {
        return associatedDevice;
    }
}
