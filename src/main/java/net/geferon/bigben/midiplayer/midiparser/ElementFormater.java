package net.geferon.bigben.midiplayer.midiparser;

import javax.sound.midi.Sequence;

public class ElementFormater {
    /**
     * Convert division type to name
     *
     * @param divType The type of the division
     * @return The name of the division
     */
    public static String getDivisionName(float divType) {
        if (divType == Sequence.PPQ) {
            return "PPQ";
        } else if (divType == Sequence.SMPTE_24) {
            return "SMPTE, 24 frames per second";
        } else if (divType == Sequence.SMPTE_25) {
            return "SMPTE, 25 frames per second";
        } else if (divType == Sequence.SMPTE_30DROP) {
            return "SMPTE, 29.97 frames per second";
        } else if (divType == Sequence.SMPTE_30) {
            return "SMPTE, 30 frames per second";
        }

        return String.format("(%.2f)", divType);
    }

}
