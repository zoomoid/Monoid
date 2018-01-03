package synth.ui.composition;

import org.jetbrains.annotations.TestOnly;
import synth.modulation.Envelope;
import synth.ui.components.swing.BlankKnob;
import synth.ui.components.swing.BlankLabel;
import synth.ui.components.swing.BlankPanel;

import javax.swing.*;
import java.awt.*;

public class EnvelopePanel extends BlankPanel {

    private Envelope envelope;
    private BlankKnob a, d, s, r;
    private BlankKnob mod;
    private BlankPanel labelPanel, parameterPanel;
    private BlankLabel label;
    public EnvelopePanel(Envelope envelope, String text){
        this.envelope = envelope;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        a = new BlankKnob(new BlankKnob.Parameters(1f, 5000, 5, false, false), BlankKnob.SMALL, 5f, "A");
        d = new BlankKnob(new BlankKnob.Parameters(1f, 5000, 5, false, false), BlankKnob.SMALL, 100f, "D");
        s = new BlankKnob(new BlankKnob.Parameters(0f, 1f, 0.01f, false, true), BlankKnob.SMALL, 0.75f, "S");
        r = new BlankKnob(new BlankKnob.Parameters(1f, 5000, 5, false, false), BlankKnob.SMALL, 300f, "R");
        mod = new BlankKnob(new BlankKnob.Parameters(-100f, 100f, 1f, false, true), BlankKnob.SMALL, 0f, "MOD");
        label = new BlankLabel(text);
        label.setFont(new Font("Fira Mono", Font.BOLD, 10));
        labelPanel = new BlankPanel();
        labelPanel.setLayout(new BorderLayout(0,0));
        labelPanel.add(label);
        parameterPanel = new BlankPanel();
        a.addPropertyChangeListener(e -> {
            float t = (float)e.getNewValue();
            envelope.setAttack((int)t);
        });

        d.addPropertyChangeListener(e -> {
            float t = (float)e.getNewValue();
            envelope.setDecay((int)t);
        });

        s.addPropertyChangeListener(e -> {
            float t = (float)e.getNewValue();
            envelope.setSustain((int)t);
        });

        r.addPropertyChangeListener(e -> {
            float t = (float)e.getNewValue();
            envelope.setRelease((int)t);
        });

        mod.addPropertyChangeListener(e -> {
            envelope.setModulationStrength((float)(e.getNewValue()) / 100);
        });

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);

        parameterPanel.add(mod);
        parameterPanel.add(separator);
        parameterPanel.add(a);
        parameterPanel.add(d);
        parameterPanel.add(s);
        parameterPanel.add(r);

        this.add(labelPanel);
        this.add(parameterPanel);

        initializeFromComponent();
    }

    private void initializeFromComponent(){
        if(this.envelope != null){
            a.setValue(envelope.attack());
            d.setValue(envelope.decay());
            s.setValue(envelope.sustain());
            r.setValue(envelope.release());
            mod.setValue(envelope.getModulationStrength() * 100);
        }
    }

    @TestOnly
    public void test(){
        assert (envelope.attack() == 5 && envelope.decay() == 0 && envelope.sustain() == 1f && envelope.release() == 20);
        this.a.setValue(200);
        this.d.setValue(300);
        this.s.setValue(1f);
        this.r.setValue(1000);
        this.mod.setValue(100);
        assert (envelope.attack() == 200 && envelope.decay() == 300 && envelope.sustain() == 0.5f && envelope.release() == 1000 && envelope.getModulationStrength() == 1);
        System.out.println("TEST SUCCESSFUL");

    }
}
