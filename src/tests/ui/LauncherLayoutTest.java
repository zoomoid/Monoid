package tests.ui;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Pitch;
import net.beadsproject.beads.ugens.RangeLimiter;
import synth.auxilliary.ContextProvider;
import synth.auxilliary.SignalProcessor;
import synth.filter.Filter;
import synth.filter.models.BiquadFilter;
import synth.modulation.ModulationOscillator;
import synth.osc.BasicOscillator;
import synth.osc.SmartOscillator;
import synth.ui.composition.PresetPanel;
import synth.ui.providers.AdditiveUIProvider;
import synth.ui.FilterUI;
import synth.ui.ModulationUI;
import synth.ui.OscillatorUI;
import synth.ui.components.Canvas;
import synth.ui.components.swing.BlankPanel;
import synth.ui.components.swing.BlankToggleButton;

import javax.swing.*;
import java.awt.*;

public class LauncherLayoutTest {
    public static void main(String args[]) {
        PresetPanel main = new PresetPanel("Monoid");
    }
}
