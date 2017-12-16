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
 * MidiController.
 * 
 * This class encapsulates a MIDI device that is used for 
 * knob and slider input.
 * 
 * I make no guarantees about this code, except that it
 * works well for using my nanoKontrol to control
 * Java synthesizers.
 * 
 * Run the MidiControllerConfigure program to configure
 * your MIDI controller, or just call the configure
 * method on an instance of this class.
 * 
 * @author Evan X. Merz
 *
 */
public class MidiController
{
	private String deviceName = "nanoKONTROL 1 SLIDER/KNOB";
	private int sliderCount = 9;
	private int knobCount = 9;
	
	private MidiDevice nkDevice;
	private Transmitter nkTransmitter;
	
	private ArrayList<ActionListener> listeners;
	
	private Queue<ShortMessage> midiQueue;
	public ShortMessage mostRecentMidiEvent;
	
	// knob/slider/buttom numbers
	private boolean configured = false;
	public HashMap<Integer, Integer> knobControl;
	public HashMap<Integer, Integer> sliderControl;
	
	public MidiController()
	{
		listeners = new ArrayList<ActionListener>();
		midiQueue = new LinkedList<ShortMessage>();
		mostRecentMidiEvent = null;
		
		openDevice();
		
		// load mappings from file if the file exists
		configured = false;
		knobControl = new HashMap<Integer, Integer>();
		sliderControl = new HashMap<Integer, Integer>();
		try
		{
			String configFilename = System.getProperty("user.home") + "\\MidiController.java.cfg";
			File cfg = new File(configFilename);
			if( cfg.exists() )
			{
				Scanner in = new Scanner(cfg);
				
				// read the device name
				deviceName = in.nextLine();
				// read the number of sliders
				sliderCount = Integer.parseInt(in.nextLine());
				// read the number of knobs
				knobCount = Integer.parseInt(in.nextLine());
				
				// read all slider controller numbers
				for( int i = 0; i < sliderCount; i++ )
				{
					//sliderControl[i] = Integer.parseInt(in.nextLine());
					sliderControl.put(Integer.parseInt(in.nextLine()), i);
				}
				
				// read all knob controller numbers
				for( int i = 0; i < knobCount; i++ )
				{
					//knobControl[i] = Integer.parseInt(in.nextLine());
					knobControl.put(Integer.parseInt(in.nextLine()), i);
				}

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
	    		//ActionEvent ae = new ActionEvent(this, 1, "nanokontrol");
	    		// 2015-01-24 - changed for more versatility
	    		ActionEvent ae = new ActionEvent(sm, 1, "controller");
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
		System.out.print("Enter the number for the device you want to use for MIDI controller input (knobs and sliders): ");
		int newDeviceNumber = Integer.parseInt(in.nextLine());
		deviceName = infos[newDeviceNumber - 1].getName();
		
		// Prompt for the number of knobs and the number of sliders
		System.out.print("Enter the number of sliders: ");
		sliderCount = Integer.parseInt(in.nextLine());
		System.out.print("Enter the number of knobs: ");
		knobCount = Integer.parseInt(in.nextLine());
		
		// open the selected device
		openDevice();
		if( nkTransmitter != null )
		{
			int[] sliderControl = new int[sliderCount];
			int[] knobControl = new int[knobCount];
			
			ConfigureReceiver cr = new ConfigureReceiver();
			nkTransmitter.setReceiver(cr);
			
			// prompt the user
			for( int i = 0; i < sliderCount; i++ )
			{
				System.out.print("Move slider " + (i + 1) + " then press enter:");
				in.nextLine();
				sliderControl[i] = cr.lastController;
				
				System.out.println(cr.lastController);
			}
			for( int i = 0; i < knobCount; i++ )
			{
				System.out.print("Move knob " + (i + 1) + " then press enter:");
				in.nextLine();
				knobControl[i] = cr.lastController;
				
				System.out.println(cr.lastController);
			}
			in.close();
			
			try
			{
				// save to file
				String outputFilename = System.getProperty("user.home") + "\\MidiController.java.cfg";
				PrintWriter out = new PrintWriter(outputFilename);
				out.println(deviceName);
				out.println(sliderCount);
				out.println(knobCount);
				for( int i = 0; i < sliderCount; i++ )
				{
					out.println(sliderControl[i]);
				}
				for( int i = 0; i < knobCount; i++ )
				{
					out.println(knobControl[i]);
				}

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
			type = "other";
			index = sm.getData1();
			value = sm.getData2();
			
			int data1 = sm.getData1();
			
			if( sliderControl.containsKey(data1) )
			{
				index = sliderControl.get(data1);
				type = "slider";
			}
			if( knobControl.containsKey(data1) )
			{
				index = knobControl.get(data1);
				type = "knob";
			}
		}
		
		@Override
		public String toString()
		{
			return "MidiInputEvent[type=" + this.type + ", index=" + this.index + ", value=" + this.value + "]";
		}
	}
	

}
