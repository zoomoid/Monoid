package synth.ui.composition;

import net.beadsproject.beads.data.Buffer;
import synth.SynthController;
import synth.modulation.LFO;
import synth.osc.WavetableOscillator;
import synth.ui.OscillatorUI;
import synth.ui.components.swing.*;

import javax.swing.*;
import java.awt.*;

public class LFOPanel extends BlankPanel {

    public class WaveType extends SpinnerImageContainer {

        public WaveType(ImageIcon img, int index){
            if(img == null){
                throw new IllegalArgumentException("WaveType expects icon to be not null");
            }
            this.index = index;
            this.img = img.getImage();
        }

        public WaveType(String path, int index){
            if(path == null){
                throw new IllegalArgumentException("WaveType expects path to be not null");
            }
            this.img = new ImageIcon((SynthController.class.getResource("assets/" + path)).getPath()).getImage();
            if(this.img == null){
                throw new IllegalArgumentException("Cannot load image from given path");
            }
            this.index = index;
        }

        public WaveType(String path){
            if(path == null){
                throw new IllegalArgumentException("WaveType expects path to be not null");
            }
            this.img = new ImageIcon((SynthController.class.getResource("assets/" + path)).getPath()).getImage();
            if(this.img == null){
                throw new IllegalArgumentException("Cannot load image from given path");
            }
            this.index = staticIndex;
            staticIndex++;
        }
    }

    private WaveType[] icons = {
        new WaveType("sine.png", 0),
        new WaveType("triangle.png",1),
        new WaveType("saw.png",2),
        new WaveType("square.png", 3),
        new WaveType("noise.png", 4)
    };

    private LFO lfo;
    private BlankKnob f, a;
    private BlankImageSpinner t;
    private BlankKnob mod;
    private BlankPanel labelPanel, parameterPanel;
    private BlankLabel label;
    public LFOPanel(LFO lfo, String text){
        this.lfo = lfo;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        a = new BlankKnob(new BlankKnob.Parameters(0f, 1f, 0.01f, false, false), BlankKnob.SMALL, 5f, "AMP");
        f = new BlankKnob(new BlankKnob.Parameters(0f, 100f, 1, false, false), BlankKnob.SMALL, 100f, "FREQ");
        mod = new BlankKnob(new BlankKnob.Parameters(-100f, 100f, 1f, false, true), BlankKnob.SMALL, 0f, "MOD");
        label = new BlankLabel(text);
        label.setFont(new Font("Fira Mono", Font.BOLD, 10));
        labelPanel = new BlankPanel();
        labelPanel.setLayout(new BorderLayout(0,0));
        labelPanel.add(label);
        parameterPanel = new BlankPanel();
        a.addPropertyChangeListener(e -> {
            float t = (float)e.getNewValue();
            lfo.setAmplitude(t);
        });

        f.addPropertyChangeListener(e -> {
            float t = (float)e.getNewValue();
            lfo.setFrequency(t);
        });

        t = new BlankImageSpinner(new SpinnerListModel(icons));
        t.addChangeListener(e -> {
            int value = ((OscillatorUI.WaveType)((JSpinner)e.getSource()).getValue()).getIndex();
            switch(value){
                case 0:
                    lfo.setType(LFO.Type.SINE);
                    break;
                case 1:
                    lfo.setType(LFO.Type.TRIANGLE);
                    break;
                case 2:
                    lfo.setType(LFO.Type.SAW);
                    break;
                case 3:
                    lfo.setType(LFO.Type.SQUARE);
                    break;
                case 4:
                    lfo.setType(LFO.Type.NOISE);
                    break;
                default: break;
            }
        });

        mod.addPropertyChangeListener(e -> {
            lfo.setModulationStrength((float)(e.getNewValue()) / 100);
        });

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);

        parameterPanel.add(mod);
        parameterPanel.add(separator);
        parameterPanel.add(f);
        parameterPanel.add(a);

        this.add(labelPanel);
        this.add(parameterPanel);

        initializeFromComponent();
    }

    private void initializeFromComponent(){
        if(this.lfo != null){
            a.setValue(lfo.amplitude());
            f.setValue(lfo.frequency());
            t.setValue(this.setLFOType(lfo.type()));
            mod.setValue(lfo.getModulationStrength() * 100);
        }
    }

    public void test(){
        this.a.setValue(200);
        this.f.setValue(300);
        this.t.setValue(1f);
        this.mod.setValue(100);
    }

    private WaveType setLFOType(LFO.Type type){
        switch(type){
            case SINE : return icons[0];
            case TRIANGLE : return icons[1];
            case SAW : return icons[2];
            case SQUARE : return icons[3];
            case NOISE : return icons[4];
            default : return icons[0];
        }
    }
}
