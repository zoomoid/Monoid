package synth.controller;

import synth.SynthController;

import javax.sound.midi.ShortMessage;

public class Controller {

    SynthController context;

    public Controller(SynthController context){
        this.context = context;
    }

    public void sendMidiToBus(ShortMessage message){
        //context.getBusses().toAll(message);
    }

}
