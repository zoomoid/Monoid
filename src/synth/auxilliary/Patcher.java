package synth.auxilliary;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Patcher {
    public Patcher(){

    }

    /**
     * Patches in input to and output
     * @param input input UGen
     * @param output output UGen
     */
    public static void patchSingle(UGen input, UGen output){
        try {
            output.addInput(input);
        } catch(NullPointerException e){
            System.out.println("Output not existing");
        }

    }

    /**
     * Routes several inputs to a single output
     * @param inputs array of inputs
     * @param output output to patch to
     */
    public static void patchMultiple(UGen[] inputs, UGen output){
        if(inputs != null){
            for (UGen input: inputs) {
                if(input != null && output != null) {
                    output.addInput(input);
                }
            }
        }
    }

    public static void patchMultiple(LinkedList<WavePlayer> inputs, UGen output){
        if(inputs != null && output != null){
            ListIterator<WavePlayer> iterator = inputs.listIterator();
            while(iterator.hasNext()){
                UGen e = iterator.next();
                if(e != null){
                    output.addInput(e);
                }
            }
        }
    }
}
