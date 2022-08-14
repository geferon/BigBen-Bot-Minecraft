package net.geferon.bigben.midiplayer.instruments;

import java.util.HashMap;
import java.util.Map;

public class InstrumentMap {
    /**
     * All known instruments map
     */
    private static final Map<Integer, Instrument> s_instruments = new HashMap<Integer, Instrument>();


    /**
     * The drum machine mapping
     */
    private static final Map<Integer, InstrumentEntry> s_drumMap = new HashMap<Integer, InstrumentEntry>();


    /**
     * Default/fallback instrument
     */
    private static Instrument s_defaultInstrument;


    /**
     * Default drum instrument
     */
    private static InstrumentEntry s_defaultDrum;

    static {
        InstrumentEntry instrument = new InstrumentEntry("note.harp", 1.0f);
        Map<OctaveDefinition, InstrumentEntry> octaves = new HashMap<OctaveDefinition, InstrumentEntry>();

        for (int i = 0; i < 11; i += 2) {
            octaves.put(new OctaveDefinition(i, i + 1), instrument);
        }

        s_defaultInstrument = new Instrument(octaves);
        s_defaultDrum = new InstrumentEntry("note.bd", 1.0f);
    }


    /**
     * MTA access mutex
     */
    private static final Object s_mutex = new Object();


    /**
     * Get instrument for MIDI program
     * @param program The program to use
     * @return The instrument for this MIDI program
     */
    public static Instrument getInstrument(int program) {
        synchronized (s_mutex) {
            Instrument instrument = s_instruments.getOrDefault(program, s_defaultInstrument);

            return instrument;
        }
    }


    /**
     * Get the drum instrument
     * @param key The key to find this instrument
     * @return The selected drum instrument
     */
    public static InstrumentEntry getDrum(int key) {
        synchronized (s_mutex)
        {
            InstrumentEntry instrument = s_drumMap.getOrDefault(key, s_defaultDrum);

            return instrument;
        }
    }


    /**
     * Get default instrument
     * @return The default instrument
     */
    public static Instrument getDefault() {
        synchronized (s_mutex) {
            return s_defaultInstrument;
        }
    }

    /**
     * Set the instrument map
     *
     * @param instruments The instruments in the map
     * @param defaultInstrument The default instrument
     */
    public static void set(Map<Integer, Map<OctaveDefinition, InstrumentEntry>> instruments,
                           Map<OctaveDefinition, InstrumentEntry> defaultInstrument) {
        synchronized (s_mutex) {
            s_instruments.clear();
            for (Map.Entry<Integer, Map<OctaveDefinition, InstrumentEntry>> entrySet : instruments.entrySet()) {
                s_instruments.put(entrySet.getKey(), new Instrument(entrySet.getValue()));
            }

            s_defaultInstrument = new Instrument(defaultInstrument);
        }
    }




    /**
     * Set the drum map
     *
     * @param drums The drum instruments in the drum map
     * @param defaultDrum The default drum instrument
     */
    public static void set(Map<Integer, InstrumentEntry> drums, InstrumentEntry defaultDrum) {
        synchronized (s_mutex) {
            s_drumMap.clear();
            for (Map.Entry<Integer, InstrumentEntry> entry : drums.entrySet())
            {
                s_drumMap.put(entry.getKey(), entry.getValue());
            }
            s_defaultDrum = defaultDrum;
        }
    }
}
