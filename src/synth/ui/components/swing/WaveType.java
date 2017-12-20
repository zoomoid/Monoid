package synth.ui.components.swing;

import synth.SynthController;
import javax.swing.*;
import java.net.URL;

public class WaveType extends SpinnerImageContainer {

    public static int staticIndex = 0;

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