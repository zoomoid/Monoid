package synth.midi;

import synth.osc.Oscillator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class MidiFileReader{
    /**The Midi File, can be NULL if there is an error */
    public File midiFile;

    /**The controlled Oscillator */
    Oscillator controlledOsc;

    /**The FileInputStream to read bytes and convert them to hex */
    FileInputStream reader;

    /** Necessary variables containing basic information about the midi file */
    int tracks; //number of tracks
    int fileFormat; //single track, synchronous tracks, asynchronous tracks (see analyze header)
    int deltaTicks; //TODO add reference

    /**
     *  List of lists for each track, holding the midi commands, where every command ia an int array of following order
     *  0: delta time
     *  1: command and channel (0 - 15)
     *  2-pinf: values
     * */
    ArrayList<LinkedList<int[]>> trackLists;

    //TODO rework javadoc
    /**Basic Constructor */
    public MidiFileReader(File midiFile, Oscillator osc) {
        this.midiFile = midiFile;
        this.controlledOsc = osc;
        try {
            reader = new FileInputStream(this.midiFile);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File not found");
        }
        try {
            analyzeHeader();
        } catch (NoMidiFileException nomid) {
            this.midiFile = null;
        }
        if(this.midiFile != null) {
            trackLists = new ArrayList<>();
            for(int i = 0; i < tracks; i++) {
                trackLists.add(new LinkedList<int[]>());
            }
        }
    }

    /**
     * converts a String holding an hexadecimal number into an int
     * @param hex String, holding hexadecimal number
     * @return hexadecimal number as int
     */
    public int hexToInt(String hex) {
        int result = 0;
        for(int i = 0; i < hex.length(); i++) {
            char currChar = hex.substring(i, i).toCharArray()[0];
            //result is successed by the current value powered by the current digit in the number
            result += Math.pow(hexCharToInt(currChar), Math.pow(16, hex.length() - i - 1));
        }
        return result;
    }

    /**
     * Checks whether the midiFile header is correct or not
     * @throws NoMidiFileException if the midiFile header is not a header of a midiFile
     */
    public void analyzeHeader() throws NoMidiFileException{
        //check whether midi header is correct or not
        byte[] header = new byte[14];
        /*
        14 is header length
        if header length is smaller than 14, the file cant be a midi file
        First four bytes are 4d 54 68 64 (ASCII MThd)
        Following four bytes are 00 00 00 06 (6, length of actual midi header)
        Last six bytes are ff ff nn nn dd dd where
        ff ff is the file format
            0 - single track
            1 - multiple, synchronous tracks
            2 - multiple tracks, asynchronous
         nn nn is the number of tracks in the midi file
         dd dd is the number of delta ticks per quarter note
         */
        try {
            if (this.reader.read(header, 0, 14) == 14) {
                int headerValue = header[0] * 1000 + header[1] * 100 + header[2] * 10 + header[3];
                //check if header is correct
                if(headerValue != hexToInt("4d546864")) {
                    System.out.println("Wrong Midi header");
                    throw new NoMidiFileException();
                } else if(header[7] != 6) { //check if length parameter in the header ie correct
                    System.out.println("Wrong length parameter in midi header");
                    throw new NoMidiFileException();
                }
                //setup values
                this.fileFormat = header[8] * 10 + header[9];
                this.tracks = header[10] * 10 + header[11];
                this.deltaTicks = header[12] * 10 + header[13];
            } else {
                System.out.println("Wrong header length");
                throw new NoMidiFileException();
            }
        } catch(IOException e) {
            throw new NoMidiFileException();
        }
    }

    /**
     * Analyzes track header i in chronological order and calls fillTrackList
     * @param index needed for call of fllTrackList function
     * @throws NoMidiFileException
     */
    public void analyzeTrackHeader(int index) throws NoMidiFileException{
        //check, whether track header is correct
        if(isMidiFileCorrect()) {
            byte[] header = new byte[8]; //8 is track header length
            try{
                if(this.reader.read(header, 0, 8) == 8) {
                    int headerValue = header[0] * 1000 + header[1] * 100 + header[2] * 10 + header[3];
                    //check if header is correct
                    if(headerValue != hexToInt("4d54726b")) { //ASCII 'MTrk'
                        System.out.println("Wrong Track header in track header check");
                        throw new NoMidiFileException();
                    } else {
                        int length = header[4] * 1000 + header[5] * 100 + header[6] * 10 + header[7];
                        fillTrackList(index, length);
                    }
                }
            } catch (IOException e) {
                System.out.println("Wrong Track Header");
                throw new NoMidiFileException();
            }
        }
    }

    //TODO maybe replace List<int[]> with int[][]
    /**
     * Fills the list of commands of track with index index with arrays, that are one midi command or meta event
     * @param index The index of the track
     * @param length The length of the midi command list
     */
    private void fillTrackList(int index, int length) {
        //length is equal to number of midi commands
        LinkedList<Integer[]> track = new LinkedList<Integer[]>();
        int read = 0; //save last read byte here
        boolean readDeltaTime = true; //assuming first midi command starts with a delta time in front
        int currentDeltaTime = 0;
        while(read != -1 && length > 0) {
            //get the next byte
            try {
                read = this.reader.read();
                length--;
            } catch (IOException e) {
                //TODO test, if java continues with trying to read the same byte again or reading the next one
                continue;
            }

            //use list of integer, because it is not clear how many bytes belong to the next command
            LinkedList<Integer> command = new LinkedList<>();

            /*
            * put into String form and check if it equals a new track header, should not occur, because of parameter
            * length
            */
            if(command.size() == 4) {
                String maybeHeader = command.get(0).toString() + command.get(1).toString() + command.get(2).toString() + command.get(3).toString();
                if(maybeHeader.equals("4d54726b")) { //see analyzeTrackHeader
                    break;
                }
            }

            if(!(currentDeltaTime == 0 && read >= 248 && read <= 251)) {
                if (read < 128) {
                    currentDeltaTime += read;
                    command.addFirst(currentDeltaTime);
                    //reset delta time
                    currentDeltaTime = 0;
                    readDeltaTime = false;
                } else if (readDeltaTime) {
                    //success delta time by current delta time chunk
                    currentDeltaTime += (read & 127);
                    continue; //next byte is delta time chunk, so continue
                }
            }

            //check whether next command is midi command or meta event
            //meta event
            if(read == 255) {
                command.add(read);
                continue;
            } else if(read >= 128) { //midi command
                //system messages TODO add system messages
                command.add(read);
                if(read < 208 || read > 223 && read < 248 || read > 251) {
                    addNextBytes(command, 3);
                } else if(read < 248 || read > 251) {
                    addNextBytes(command, 2);
                }
            } else { //part two of the meta event
                command.add(read); //indicator
                command.add(read); //number of data bytes
                addNextBytes(command, read); //data bytes
            }

            //setup command as array
            int midiCommand[] = new int[command.size()];
            for(int i = 0; i < command.size(); i++) {
                midiCommand[i] = command.get(i);
            }
            LinkedList<int[]> currCommandList = trackLists.get(index);
            currCommandList.add(midiCommand);
            trackLists.set(index, currCommandList);
        }
    }

    /**
     * Adds next numberOfBytes' Bytes to List list
     * @param list The list, which the bytes are appended to
     * @param numberOfBytes The number of bytes to append, reader saves, where the next byte is
     */
    public void addNextBytes(LinkedList<Integer> list, int numberOfBytes) {
        for(int i = 0; i < numberOfBytes; i++) {
            try {
                int read = this.reader.read();
                list.add(read);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * Converts a single char as a hexadecimal number into an int
     * @param hex char as hexadecimal number
     * @return hex as int
     */
    public int hexCharToInt(char hex) {
        int result = -1; //-1 means error, hex char cant be converted to int
        //convert hex char to int
        switch(hex) {
            case 'f': case 'F': return 15;
            case 'e': case 'E': return 14;
            case 'd': case 'D': return 13;
            case 'c': case 'C': return 12;
            case 'b': case 'B': return 11;
            case 'a': case 'A': return 10;
            case '9': case'8': case'7': case '6': case '5': case '4': case '3': case '2': case '1': case '0': return Integer.parseInt(Character.toString(hex));
            default: break;
        }
        return result;
    }

    /**
     * Checks whether midiFile is correct
     * @return true, if midiFile is correct, false if its not. midiFile is null then
     */
    public boolean isMidiFileCorrect() {
        if(midiFile == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the indexth track of the midi file
     * @param index the tracks index
     * @return List, containing the midi commands of the track
     */
    public LinkedList<int[]> getTrack(int index) {
        return trackLists.get(index);
    }
}
