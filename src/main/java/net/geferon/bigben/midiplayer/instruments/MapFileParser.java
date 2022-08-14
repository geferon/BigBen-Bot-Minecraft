package net.geferon.bigben.midiplayer.instruments;

import net.geferon.bigben.BigBenPlugin;
import net.geferon.bigben.utils.InOutParam;
import net.geferon.bigben.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Instrument map file parser
 *
 * @author SBPrime
 */
public class MapFileParser {

    /**
     * Comment start
     */
    private final static String COMMENT = "#";

    /**
     * Load the map file from file
     *
     * @param instrumentMap The file containing the Instrument Map
     * @return Whether the map load was successful or not
     */
    public static boolean loadMap(File instrumentMap) {
        BufferedReader instrumentFile = null;

        try {
            instrumentFile = new BufferedReader(new FileReader(instrumentMap));
            return loadMap(instrumentFile);
        } catch (IOException ex) {
            BigBenPlugin.log(Level.WARNING, "Error reading file.");
            return false;
        } finally {
            if (instrumentFile != null) {
                try {
                    instrumentFile.close();
                } catch (Exception ex) {
                    //Ignore
                }
            }
        }
    }


    /**
     * Load the map file from file
     *
     * @param drumMap The file containing the Drum Map
     * @return Whether the drum map load was successful or not
     */
    public static boolean loadDrumMap(File drumMap) {
        BufferedReader drumFile = null;

        try {
            drumFile = new BufferedReader(new FileReader(drumMap));
            return loadDrumMap(drumFile);
        } catch (IOException ex) {
            BigBenPlugin.log(Level.WARNING, "Error reading file.");
            return false;
        } finally {
            if (drumFile != null) {
                try {
                    drumFile.close();
                } catch (Exception ex) {
                    //Ignore
                }
            }
        }
    }

    /**
     * Load instrument mapping from default resource
     *
     * @return Whether the map load was successful or not
     */
    public static boolean loadDefaultMap() {
        BufferedReader instrumentFile = null;

        try {
            Class<?> c = MapFileParser.class;
            InputStream isInstrument = c.getResourceAsStream("/default.map");

            instrumentFile = new BufferedReader(new InputStreamReader(isInstrument));
            return loadMap(instrumentFile);
        } catch (IOException ex) {
            BigBenPlugin.log(Level.WARNING, "Error reading file.");
            return false;
        } finally {
            if (instrumentFile != null) {
                try {
                    instrumentFile.close();
                } catch (Exception ex) {
                    //Ignore
                }
            }
        }
    }


    /**
     * Load drum mapping from default resource
     *
     * @return Whether the drum map load was successful or not
     */
    public static boolean loadDefaultDrumMap() {
        BufferedReader drumFile = null;

        try {
            Class<?> c = MapFileParser.class;
            InputStream isDrum = c.getResourceAsStream("/default.drm");

            drumFile = new BufferedReader(new InputStreamReader(isDrum));
            return loadDrumMap(drumFile);
        } catch (IOException ex) {
            BigBenPlugin.log(Level.WARNING, "Error reading file.");
            return false;
        } finally {
            if (drumFile != null) {
                try {
                    drumFile.close();
                } catch (Exception ex) {
                    //Ignore
                }
            }
        }
    }

    /**
     * Load and parse the map file
     *
     * @param instrumentFile The buffer containing the Instrument Map
     * @return Whether the map load was successful or not
     * @throws IOException When the buffer gives an IOException
     */
    private static boolean loadMap(BufferedReader instrumentFile) throws IOException {
        final Map<OctaveDefinition, InstrumentEntry> defaultInstruments = new HashMap<OctaveDefinition, InstrumentEntry>();
        final Map<Integer, Map<OctaveDefinition, InstrumentEntry>> instruments
                = new HashMap<Integer, Map<OctaveDefinition, InstrumentEntry>>();

        parseInstrumentMap(instrumentFile, defaultInstruments, instruments);

        if (defaultInstruments.isEmpty()) {
            BigBenPlugin.log(Level.WARNING, "No default instrument.");
            return false;
        }

        if (instruments.isEmpty()) {
            BigBenPlugin.log(Level.WARNING, "No instruments defined.");
            return false;
        }

        InstrumentMap.set(instruments, defaultInstruments);

        return true;
    }


    /**
     * Load and parse the drum map file
     *
     * @param drumFile The buffer containing the Drum Map
     * @return Whether the drum map load was successful or not
     * @throws IOException When the buffer gives an IOException
     */
    private static boolean loadDrumMap(BufferedReader drumFile) throws IOException {
        final InOutParam<InstrumentEntry> defaultDrum = InOutParam.Out();
        final HashMap<Integer, InstrumentEntry> drums = new HashMap<Integer, InstrumentEntry>();

        parseDrumMap(drumFile, defaultDrum, drums);

        if (!defaultDrum.isSet()) {
            BigBenPlugin.log(Level.WARNING, "No default drum.");
        }

        if (drums.isEmpty()) {
            BigBenPlugin.log(Level.WARNING, "No drums defined.");
        }

        InstrumentMap.set(drums, defaultDrum.isSet() ? defaultDrum.getValue() : null);

        return true;
    }

    /**
     * Parse the instrument map
     *
     * @param instrumentFile The buffer containing the Instrument Map
     * @param defaultInstrument The default instrument
     * @param instruments The instrument map
     * @throws IOException When the buffer gives an IOException
     */
    private static void parseInstrumentMap(BufferedReader instrumentFile,
                                           final Map<OctaveDefinition, InstrumentEntry> defaultInstrument,
                                           final Map<Integer, Map<OctaveDefinition, InstrumentEntry>> instruments) throws IOException {
        String line;
        while ((line = instrumentFile.readLine()) != null) {
            String cLine = line.trim().replace("\t", " ");
            if (cLine.startsWith(COMMENT)) {
                //Whole line of comments
                continue;
            }

            String[] parts = split(cLine);

            boolean hasError = false;
            boolean isDefault = false;
            InOutParam<Integer> id = InOutParam.Out();
            InOutParam<Integer> volume = InOutParam.Out();
            String patch = "";
            OctaveDefinition[] octaves = null;

            if (parts.length >= 4) {
                String sId = parts[0].trim();
                patch = parts[1].trim();
                String sVolume = parts[2].trim();

                if (!Utils.TryParseInteger(sId, id)) {
                    isDefault = sId.equalsIgnoreCase("D");
                    hasError |= !isDefault;
                }

                if (sVolume.endsWith("%")) {
                    if (!Utils.TryParseInteger(sVolume.substring(0, sVolume.length() - 1), volume)) {
                        hasError = true;
                    }
                } else {
                    hasError = true;
                }

                octaves = parseOctaves(parts);
                hasError |= patch.isEmpty() | !volume.isSet() | octaves == null;
            } else {
                hasError = true;
            }

            if (hasError) {
                BigBenPlugin.log(Level.WARNING, "Invalid instrument mapping line: " + line);
            } else if (isDefault && Utils.containsAny(defaultInstrument.keySet(), octaves)) {
                BigBenPlugin.log(Level.WARNING, "Duplicate default instrument entry: " + line);
            } else if (!isDefault && instruments.containsKey(id.getValue())
                    && Utils.containsAny(instruments.get(id.getValue()).keySet(), octaves)) {
                BigBenPlugin.log(Level.WARNING, "Duplicate instrument entry: " + line);

            } else {
                InstrumentEntry i = new InstrumentEntry(patch, volume.getValue() / 100.0f);

                Map<OctaveDefinition, InstrumentEntry> hash;
                if (isDefault) {
                    hash = defaultInstrument;
                } else {
                    int iid = id.getValue();
                    if (instruments.containsKey(iid)) {
                        hash = instruments.get(iid);
                    } else {
                        hash = new HashMap<OctaveDefinition, InstrumentEntry>();
                        instruments.put(iid, hash);
                    }
                }

                for (OctaveDefinition octave : octaves) {
                    hash.put(octave, i);
                }
            }
        }
    }

    /**
     * Parse the drum map
     *
     * @param drumFile The buffer containing the Drum Map
     * @param defaultDrum The default drum
     * @param drums The drums map
     * @throws IOException When the buffer gives an IOException
     */
    private static void parseDrumMap(BufferedReader drumFile,
                                     InOutParam<InstrumentEntry> defaultDrum,
                                     HashMap<Integer, InstrumentEntry> drums) throws IOException {
        String line;
        while ((line = drumFile.readLine()) != null) {
            String cLine = line.trim().replace("\t", " ");
            if (cLine.startsWith(COMMENT)) {
                //Whole line of comments
                continue;
            }

            String[] parts = split(cLine);

            boolean hasError = false;
            boolean isDefault = false;
            InOutParam<Integer> id = InOutParam.Out();
            InOutParam<Integer> volume = InOutParam.Out();
            String patch = "";

            if (parts.length >= 3) {
                String sId = parts[0].trim();
                patch = parts[1].trim();
                String sVolume = parts[2].trim();

                if (!Utils.TryParseInteger(sId, id)) {
                    isDefault = sId.equalsIgnoreCase("D");
                    hasError |= !isDefault;
                }

                if (sVolume.endsWith("%")) {
                    if (!Utils.TryParseInteger(sVolume.substring(0, sVolume.length() - 1), volume)) {
                        hasError = true;
                    }
                } else {
                    hasError = true;
                }

                hasError |= patch.isEmpty() | !volume.isSet();
            } else {
                hasError = true;
            }

            if (hasError) {
                BigBenPlugin.log(Level.WARNING, "Invalid drum mapping line: " + line);
            } else if (isDefault && defaultDrum.isSet()) {
                BigBenPlugin.log(Level.WARNING, "Duplicate default drum entry: " + line);
            } else if (!isDefault && drums.containsKey(id.getValue())) {
                BigBenPlugin.log(Level.WARNING, "Duplicate drum entry: " + line);
            } else {
                InstrumentEntry i = new InstrumentEntry(patch, volume.getValue() / 100.0f);

                if (isDefault) {
                    defaultDrum.setValue(i);
                } else {
                    drums.put(id.getValue(), i);
                }
            }
        }
    }

    /**
     * Split line and ignore comments
     *
     * @param line The line to be split
     * @return An array containing the split line
     */
    private static String[] split(String line) {
        if (line == null) {
            return new String[0];
        }

        List<String> parts = new ArrayList<String>();
        for (String s : line.split(" ")) {
            s = s.trim();
            if (!s.isEmpty()) {
                if (s.startsWith(COMMENT)) {
                    break;
                }

                parts.add(s);
            }
        }

        return parts.toArray(new String[0]);
    }

    /**
     * Parse the octaves entries
     *
     * @param parts The entries to parse
     * @return An array containing the definition of the octaves
     */
    private static OctaveDefinition[] parseOctaves(String[] parts) {
        List<OctaveDefinition> result = new ArrayList<OctaveDefinition>();
        for (int i = 3; i < parts.length; i++) {
            String s = parts[i];

            InOutParam<Integer> from = InOutParam.Out();
            if (Utils.TryParseInteger(s, from)) {
                result.add(new OctaveDefinition(from.getValue(), from.getValue()));
            } else {
                String[] elements = s.split("-");
                if (elements == null || elements.length != 2) {
                    return null;
                }

                InOutParam<Integer> to = InOutParam.Out();
                if (!Utils.TryParseInteger(elements[0], from)
                        || !Utils.TryParseInteger(elements[1], to)) {
                    return null;
                }

                result.add(new OctaveDefinition(from.getValue(), to.getValue()));
            }
        }

        return result.toArray(new OctaveDefinition[0]);
    }
}