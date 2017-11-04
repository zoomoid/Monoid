package synth.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.midi.ShortMessage;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.WavePlayer;
import synth.controller.external.MidiKeyboard;

public class MIDIKeyboard {

    private Controller context;

    public MIDIKeyboard(Controller context){
        this.context = context;
        MidiKeyboard keys = new MidiKeyboard();
        keys.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // if the event is not null
                if( e != null )
                {
                    // if the event is a MIDI event
                    if( e.getSource() instanceof ShortMessage )
                    {
                        // get the MIDI event
                        ShortMessage sm = (ShortMessage)e.getSource();

                        context.sendMidiToBus(sm);
                    }
                }
            }
        });
    }

}
