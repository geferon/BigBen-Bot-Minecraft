package net.geferon.bigben.utils;

/**
 * This is a helper class that allows you to add output (and input) parameters
 * to java functions
 *
 * @param <T> The type of the parameter
 */
public class InOutParam<T> {

    /**
     * Initialize reference parameter (in and out value)
     *
     * @param <T> The type of the parameter
     * @param value The value of the parameter
     * @return An instance for this parameter
     */
    public static <T> InOutParam<T> Ref(T value) {
        return new InOutParam<T>(value);
    }

    /**
     * Initialize output param (out only)
     *
     * @param <T> The type of the parameter
     * @return An instance for this parameter
     */
    public static <T> InOutParam<T> Out() {
        return new InOutParam<T>();
    }

    /**
     * Is the value set
     */
    private boolean m_isSet;

    /**
     * The parameter value
     */
    private T m_value;

    /**
     * Create new instance of ref param
     *
     * @param value The value of the parameter
     */
    private InOutParam(T value) {
        m_value = value;
        m_isSet = true;
    }

    /**
     * Create new instance of out param
     */
    private InOutParam() {
        m_isSet = false;
    }

    /**
     * Get the parameter value
     *
     * @return The value of the parameter
     */
    public T getValue() {
        if (m_isSet) {
            return m_value;
        }

        throw new IllegalStateException("Output parameter not set");
    }

    public void setValue(T value) {
        m_isSet = true;
        m_value = value;
    }

    /**
     * Is the value set
     *
     * @return Whether the parameter has a value or not
     */
    public boolean isSet() {
        return m_isSet;
    }
}
