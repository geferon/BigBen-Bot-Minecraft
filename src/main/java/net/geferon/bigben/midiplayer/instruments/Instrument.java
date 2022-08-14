package net.geferon.bigben.midiplayer.instruments;

import net.geferon.bigben.utils.InOutParam;

import java.util.Map;

public class Instrument {
    /**
     * Instrument entries for octaves
     */
    private final Map<OctaveDefinition, InstrumentEntry> m_octaveEntries;


    public Instrument(Map<OctaveDefinition, InstrumentEntry> octaveEntries) {
        m_octaveEntries = octaveEntries;
    }


    /**
     * Get instrument entry for provided octave (first in range)
     * @param octave The octave
     * @param startOctave The instrument entry starting midi octave
     * @return Instrument entry
     */
    public InstrumentEntry getEntry(int octave, InOutParam<Integer> startOctave) {
        for (OctaveDefinition od : m_octaveEntries.keySet()) {
            int from = od.getFrom();
            int to = od.getTo();
            if (from <= octave &&  octave <= to) {
                startOctave.setValue(od.getFrom());
                return m_octaveEntries.get(od);
            }
        }
        return null;
    }
}