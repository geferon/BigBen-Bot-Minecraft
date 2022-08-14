package net.geferon.bigben.midiplayer.instruments;

public class OctaveDefinition {

    private final int m_from;
    private final int m_to;

    public int getFrom() {
        return m_from;
    }

    public int getTo() {
        return m_to;
    }

    public OctaveDefinition(int from, int to) {
        m_from = from;
        m_to = to;
    }

    @Override
    public int hashCode() {
        return m_to ^ m_from;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OctaveDefinition)) {
            return false;
        }

        OctaveDefinition othre = (OctaveDefinition) obj;
        return othre.m_from == m_from && othre.m_to == m_to;
    }
}
