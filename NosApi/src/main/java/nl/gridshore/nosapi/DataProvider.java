package nl.gridshore.nosapi;

/**
 * <p>Provider for the NOS api, returns our own objects to be able to handle different api versions without to much
 * problems for clients of this provider.</p>
 * <p>All methods throw ClientExceptions.</p>
 *
 * @author Jettro Coenradie
 */
public interface DataProvider {

    /**
     * Returns information about the version of the NOS api used. Might in the future be used for returned data
     * about the version of our own api as well.
     *
     * @return Version object that contains the version data
     */
    Version obtainVersion();
}
