package synth.ui.composition;

import synth.modulation.Envelope;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;

public class EnvelopePanel extends BlankPanel {

    public EnvelopePanel(Envelope envelope){
        Envelope envelope1 = envelope;
        BlankKnob a = new BlankKnob(new BlankKnob.Parameters(1f, 5000, 5, false, false), BlankKnob.SMALL, 5f, "A");
        BlankKnob d = new BlankKnob(new BlankKnob.Parameters(1f, 5000, 5, false, false), BlankKnob.SMALL, 100f, "D");
        BlankKnob s = new BlankKnob(new BlankKnob.Parameters(0f, 1f, 0.01f, false, true), BlankKnob.SMALL, 0.75f, "A");
        BlankKnob r = new BlankKnob(new BlankKnob.Parameters(1f, 5000, 5, false, false), BlankKnob.SMALL, 300f, "R");
        BlankKnob mod = new BlankKnob(new BlankKnob.Parameters(-100f, 100f, 1f, false, true), BlankKnob.SMALL, 0f, "MOD");

        a.addPropertyChangeListener(e -> {
            envelope.setAttack((int)e.getNewValue());
        });

        d.addPropertyChangeListener(e -> {
            envelope.setDecay((int)e.getNewValue());
        });

        s.addPropertyChangeListener(e -> {
            envelope.setSustain((int)e.getNewValue());
        });

        r.addPropertyChangeListener(e -> {
            envelope.setRelease((int)e.getNewValue());
        });

        mod.addPropertyChangeListener(e -> {
            envelope.setModulationStrength((float)e.getNewValue());
        });

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);

        this.add(mod);
        this.add(separator);
        this.add(a);
        this.add(d);
        this.add(s);
        this.add(r);
    }
}
