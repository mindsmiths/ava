package utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public class Settings {
    public static final Map<String, String> env = System.getenv();
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    public static String ARMORY_SITE_URL;
    public static String MODULE;
    public static String DEFAULT_TIME_ZONE;

    static {
        ARMORY_SITE_URL = dotenv.get("ARMORY_SITE_URL", "");
        MODULE = dotenv.get("MODULE", "local");
        DEFAULT_TIME_ZONE = dotenv.get("DEFAULT_TIME_ZONE", "Europe/Zagreb");
    }

    public Settings() {
    }
}