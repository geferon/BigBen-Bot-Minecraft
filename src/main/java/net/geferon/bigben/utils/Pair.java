package net.geferon.bigben.utils;

/**
 * A simple pair
 * @param <T1>
 * @param <T2>
 */
public class Pair<T1, T2> {

    private final T1 m_x1;
    private final T2 m_x2;

    public T1 getX1() {
        return m_x1;
    }

    public T2 getX2() {
        return m_x2;
    }

    public Pair(T1 x1, T2 x2){
        m_x1 = x1;
        m_x2 = x2;
    }
}
