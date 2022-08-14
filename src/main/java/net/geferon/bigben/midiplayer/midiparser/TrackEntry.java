package net.geferon.bigben.midiplayer.midiparser;

import net.geferon.bigben.midiplayer.instruments.Instrument;
import net.geferon.bigben.midiplayer.instruments.InstrumentEntry;
import net.geferon.bigben.utils.InOutParam;

class TrackEntry {

    private long m_millis;

    private final NoteEntry m_note;

    public NoteEntry getNote() {
        return m_note;
    }

    public long getMillis() {
        return m_millis;
    }

    public void setMillis(long milis) {
        m_millis = milis;
    }

    public NoteEntry getEntry() {
        return m_note;
    }

    public TrackEntry(long millis, InstrumentEntry instrument, float volume) {
        float scale;

        final String instrumentPatch;

        if (instrument != null) {
            scale = Math.max(0, instrument.getVolumeScale());
            instrumentPatch = instrument.getPatch();
        } else {
            scale = 0.0f;
            instrumentPatch = null;
        }

        m_millis = millis;
        final float vv = Math.max(0, Math.min(1, volume * scale)) * 3.0f;

        m_note = new NoteEntry(instrumentPatch, 1.0f, vv);
    }

    public TrackEntry(long millis, Instrument instrument, int octave, int note, float volume) {
        float scale;

        InstrumentEntry iEntry;
        if (instrument == null) {
            iEntry = null;
        } else {
            InOutParam<Integer> startOctave = InOutParam.Out();
            iEntry = instrument.getEntry(octave, startOctave);
            if (iEntry != null && startOctave.isSet()) {
                octave -= startOctave.getValue();
            }
        }

        final String instrumentPatch;

        if (iEntry != null) {
            scale = Math.max(0, iEntry.getVolumeScale());
            instrumentPatch = iEntry.getPatch();
        } else {
            scale = 0.0f;
            instrumentPatch = null;
        }

        m_millis = millis;

        final float frq = (float) Math.pow(2, (note + 12 * (octave % 2) - 12.0) / 12.0);
        final float vv = Math.max(0, Math.min(1, volume * scale)) * 3.0f;

        m_note = new NoteEntry(instrumentPatch, frq, vv);
    }

    @Override
    public int hashCode() {
        return (m_note != null ? m_note.hashCode() : 0) ^
                ((Long)m_millis).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        TrackEntry other = obj instanceof TrackEntry ? (TrackEntry)obj : null;
        if (other == null)
        {
            return false;
        }

        return m_millis == other.m_millis &&
                ((m_note == null && other.m_note == null) ||
                        (m_note!=null && m_note.equals(other.m_note)));
    }


}