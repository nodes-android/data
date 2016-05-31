package dk.nodes.data.annotation;

/**
 * Created by joso on 31/05/16.
 */
public enum Mode {
    SHAREDPREFERENCES(Constants.SHARED_PREFS), FILE(Constants.FILE);


    Mode(String constant) {

    }

    public static class Constants {
        public static final String FILE = "FILE";
        public static final String SHARED_PREFS = "SHARED_PREFS";
    }
}
