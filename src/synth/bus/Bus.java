package synth.bus;

import synth.auxilliary.Device;

import java.util.ArrayList;

public abstract class Bus {

    public ArrayList<Device> targets;

    public ArrayList<Device> sources;

    public Bus(){
        targets = new ArrayList<>();
        sources = new ArrayList<>();
    }

    public void addToTargets(Device device){
        if(device != null){
            targets.add(device);
        }
    }

    public void addToSources(Device device){
        if(device != null){
            sources.add(device);
        }
    }

    public ArrayList<Device> getSources() {
        return sources;
    }

    public ArrayList<Device> getTargets() {
        return targets;
    }
}
