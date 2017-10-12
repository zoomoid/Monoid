package synth.sequencing;

import java.util.LinkedList;

public abstract class Pattern {

    LinkedList<Block> blocks;

    public Pattern(){
        blocks = new LinkedList<>();
    }

    public Pattern(LinkedList<Block> oldBlocks){
        this();
        this.blocks.addAll(oldBlocks);
    }

    public Pattern(Pattern oldPattern){
        this();
        this.blocks.addAll(oldPattern.getBlocks());
    }

    public LinkedList<Block> getBlocks() {
        return blocks;
    }

    public void append(Block b){
        this.blocks.add(b);
    }

    public class MIDIPattern extends Pattern {

        public FrequencyPattern toFrequencyPattern(){
            FrequencyPattern f = new FrequencyPattern();
            for(Block oldBlock : blocks){
                Block b = new Block(oldBlock.getDuration(), this.pitchToFrequency((int)oldBlock.getValue()));
                f.append(b);
            }
            return f;
        }

        private float pitchToFrequency(int midiPitch)
        {
            /*
             *  MIDI pitch number to frequency conversion equation from
             *  http://newt.phys.unsw.edu.au/jw/notes.html
             */
            double exponent = (midiPitch - 69.0) / 12.0;
            return (float)(Math.pow(2, exponent) * 440.0f);
	    }
    }

    public class FrequencyPattern extends Pattern {

    }

    public class Block {
        private int duration;
        private float value;

        /**
         * Creates a new Pattern Block
         * @param duration block duration in milliseconds
         * @param value block value (either frequency in Hz or MIDI value)
         */
        public Block(int duration, float value){
            this.duration = duration;
            this.value = value;
        }

        public float getValue() {
            return value;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }


}
