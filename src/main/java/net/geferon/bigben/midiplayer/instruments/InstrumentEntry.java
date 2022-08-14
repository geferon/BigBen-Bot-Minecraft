package net.geferon.bigben.midiplayer.instruments;

public class InstrumentEntry {
    /**
     * Instrument path
     */
    private final String m_patch;


    /**
     * The volume scale
     */
    private final float m_volumeScale;


    /**
     * Sound patch
     * @return The patch of this instrument
     */
    public String getPatch() {
        return m_patch;
    }

    /**
     * Get the volume scale
     * @return The volume scale for this instrument
     */
    public float getVolumeScale() {
        return m_volumeScale;
    }

    public InstrumentEntry(String patch, float volumeScale) {
        m_patch = patch;
        m_volumeScale = volumeScale;
    }
}