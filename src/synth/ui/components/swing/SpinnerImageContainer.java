package synth.ui.components.swing;

import java.awt.*;

public abstract class SpinnerImageContainer {

    protected Image img;
    protected int index;
    public static int staticIndex = 0;

    public int getIndex(){
        return index;
    }

    public Image getIcon(){
        return img;
    }

}
