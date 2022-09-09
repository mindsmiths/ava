package utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Settings {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    public static String ARMORY_SITE_URL;
    public static String MODULE;

    public Settings() {
    }

    static {
        ARMORY_SITE_URL = dotenv.get("ARMORY_SITE_URL", "");
        MODULE = dotenv.get("MODULE", "local");
    }
}