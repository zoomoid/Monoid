package synth.controller.external;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

/**
 * 
 * MidiKeyboard.
 * 
 * This class encapsulates a MIDI device that is used for 
 * piano key type input.
 * 
 * I make no guarantees about this code, except that it
 * works well for using my nanoKey to control
 * Java synthesizers.
 * 
 * Run the MidiKeyboardConfigure program to configure
 * your MIDI controller, or just call the configure
 * method on an instance of this class.
 * 
 * @author Evan X. Merz
 *
 */
public class MidiKeyboard
{
	private String deviceName = "nanoKEY 1 KEYBOARD";

	private MidiDevice nkDevice;
	private Transmitter nkTransmitter;
	
	private ArrayList<ActionListener> listeners;
	
	private Queue<ShortMessage> midiQueue;
	public ShortMessage mostRecentMidiEvent;
	
	private boolean configured = false;
	
	// some constants that define NOTE_ON and NOTE_OFF events
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	
	public MidiKeyboard()
	{
		listeners = new ArrayList<ActionListener>();
		midiQueue = new LinkedList<ShortMessage>();
		mostRecentMidiEvent = null;
		configured = false;
		
		openDevice();
		
		// load mappings from file if the file exists
		try
		{
			String configFilename = System.getProperty("user.home") + "\\MidiKeyboard.java.cfg";
			File cfg = new File(configFilename);
			if( cfg.exists() )
			{
				Scanner in = new Scanner(cfg);
				
				// read the device name
				deviceName = in.nextLine();
				
				in.close();
				
				System.out.println("Config loaded successfully.");
				configured = true;
			}
		}
		catch(Exception ex)
		{
			System.out.println("Unable to load configuration file. Try running the config program again.");
			//ex.printStackTrace();
		}
	}
	
	private void openDevice()
	{
		// Obtain information about all the installed synthesizers.
		nkDevice = null;
		nkTransmitter = null;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++)
		{
		    try
		    {
		    	MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
		        MidiDevice.Info d = infos[i];
		        
		        if( d.getName().contains(deviceName) )
		        {
		        	System.out.println("Opening " + d.getName());
		        	System.out.println("  max receivers: " + device.getMaxReceivers());
		        	System.out.println("  max transmitters: " + device.getMaxTransmitters());

		        	nkDevice = device;
		        	
		        	nkTransmitter = device.getTransmitter();
		        	nkTransmitter.setReceiver(new MidiInputReceiver(device.getDeviceInfo().getName()));
		        	
		        	nkDevice.open();
		        }
		    }
		    catch (MidiUnavailableException e)
		    {
		          // Handle or throw exception...
		    	System.out.println("Exception occurred while getting MIDI devices!");
		    	System.out.println(e.getMessage());
		    	e.printStackTrace();
		    }
		}
	}
	
	public void addActionListener(ActionListener a)
	{
		if( a != null ) listeners.add(a);
	}
	
	/**
	 * getMessage. Return the midi events in order.
	 * 
	 * @return a ShortMessage containing a midi event
	 */
	public ShortMessage getMessage()
	{
		if( !midiQueue.isEmpty() ) return midiQueue.remove();
		return null;
	}
	
	/**
	 * getMessageCount.
	 * 
	 * Get the number of stored MIDI messages.
	 * 
	 * @return the number of stored MIDI messages
	 */
	public int getMessageCount()
	{
		if( midiQueue != null ) return midiQueue.size();
		return 0;
	}
	
	/**
	 * getMidiInputEvent. This method returns a
	 * description of a MIDI event that is tailored
	 * to the KORG nanoKontrol.
	 * 
	 * @return an instance of NanokontrolEvent that describes the top midi message
	 */
	public MidiInputEvent getMidiInputEvent()
	{
		if( configured && !midiQueue.isEmpty() ) return new MidiInputEvent(midiQueue.remove());
		return null;
	}
	
	/**
	 * MidiInputReceiver. This class is the general purpos
	 * Midi interpreter that will raise ActionEvents.
	 * 
	 * @author Evan X. Merz
	 */
	class MidiInputReceiver implements Receiver
	{
	    public String name;
	    
	    public MidiInputReceiver(String name)
	    {
	        this.name = name;
	    }
	    
	    public void send(MidiMessage message, long timeStamp)
	    {
	    	try
	    	{
	    		ShortMessage sm = (ShortMessage) message;
	    		midiQueue.add(sm);
	    		mostRecentMidiEvent = sm;
	    		//System.out.println("command: " + sm.getCommand() + "  data1: " + sm.getData1() + "  data2: " + sm.getData2());
	    		
	    		// notify each listener
	    		ActionEvent ae = new ActionEvent(sm, 1, "keyboard");
	    		for( ActionListener a : listeners )
	    		{
	    			a.actionPerformed(ae);
	    		}

	    	}
	    	catch(Exception ex)
	    	{
	    		ex.printStackTrace();
	    	}
	    }
	    public void close() {}
	}
	
	// This utility learns controller numbers for each 
	// knob/slider and save the mappings to a file in the user's directory.
	public void configure()
	{
		Scanner in = new Scanner(System.in);
		
		// Give option here to select midi device (maybe not nanokontrol)
		System.out.println("Discovered MIDI devices: ");
		// Obtain information about all the installed synthesizers.
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++)
		{
		    try
		    {
		    	MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
		        MidiDevice.Info d = infos[i];
		        System.out.println((i+1) + ". " + d.getName() + " by " + d.getVendor());
		    }
		    catch (MidiUnavailableException e)
		    {
		          // Handle or throw exception...
		    	System.out.println("Exception occurred while getting MIDI devices!");
		    	System.out.println(e.getMessage());
		    	e.printStackTrace();
		    }
		}
		System.out.print("Enter the number for the device you want to use for MIDI keyboard input: ");
		int newDeviceNumber = Integer.parseInt(in.nextLine());
		this.deviceName = infos[newDeviceNumber - 1].getName();
		
		// open the selected device
		openDevice();
		if( nkTransmitter != null )
		{
			ConfigureReceiver cr = new ConfigureReceiver();
			nkTransmitter.setReceiver(cr);
			
			in.close();
			
			try
			{
				// save to file
				String outputFilename = System.getProperty("user.home") + "\\MidiKeyboard.java.cfg";
				PrintWriter out = new PrintWriter(outputFilename);
				out.println(deviceName);

				out.close();
				configured = true;
				System.out.println("Configuration file saved to " + outputFilename + ".");
			}
			catch(Exception ex)
			{
				System.out.println("Unable to save configuration file!");
				ex.printStackTrace();
			}
		}
		else
		{
			System.out.println("Unable to configure. Cannot find " + deviceName + ".");
		}
	}
	
	/**
	 * ConfigureReceiver. A Receiver used during the
	 * configuration process.
	 * 
	 * @author Evan X. Merz
	 */
	class ConfigureReceiver implements Receiver
	{
		public int lastController = -1;
		
		@Override
	    public void send(MidiMessage message, long timeStamp)
	    {
	    	try
	    	{
	    		ShortMessage sm = (ShortMessage) message;
	    		lastController = sm.getData1();
	    	}
	    	catch(Exception ex)
	    	{
	    		ex.printStackTrace();
	    	}
	    }
	    public void close() {}
	}
	
	/**
	 * MidiInputEvent. This class encapsulates a
	 * ShortMessage that has been converted into
	 * values appropriate to the Nanokontrol.
	 * 
	 * Ex:
	 * type = "slider"
	 * index = 3;
	 * value = 87;
	 * 
	 */
	public class MidiInputEvent
	{
		public String type;
		public int index;
		public int value;
		
		public MidiInputEvent(ShortMessage sm)
		{
			// set the defaults
			type = "keyboard";
			index = sm.getData1();
			value = sm.getData2();
		}
		
		@Override
		public String toString()
		{
			return "MidiInputEvent[type=" + this.type + ", index=" + this.index + ", value=" + this.value + "]";
		}
	}
	

}
