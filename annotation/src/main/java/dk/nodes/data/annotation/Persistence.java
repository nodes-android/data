package dk.nodes.data.annotation;

/**
 * Created by joso on 30/05/16.
 */
public enum Persistence {
    SERIALIZATION(Constants.SERIALIZATION), GSON(Constants.GSON);


    Persistence(String constant) {

    }

    public static class Constants {
        public static final String SERIALIZATION = "SERIALIZATION";
        public static final String GSON = "GSON";
    }
}
