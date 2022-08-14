package net.geferon.bigben.midiplayer.midiparser;

public class NoteTrack {
    private final String m_message;
    private final NoteFrame[] m_notes;

    public String getMessage() {
        return m_message;
    }

    public NoteFrame[] getNotes() {
        return m_notes;
    }

    public boolean isError() {
        return m_notes == null;
    }

    public NoteTrack(String message) {
        m_message = message;
        m_notes = null;
    }

    public NoteTrack(NoteFrame[] notes) {
        m_message = "";
        m_notes = notes;
    }
}