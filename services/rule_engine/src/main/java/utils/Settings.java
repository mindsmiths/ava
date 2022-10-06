package utils;

import java.util.Map;
import io.github.cdimascio.dotenv.Dotenv;

public class Settings {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    public static String ARMORY_SITE_URL;
    public static String MODULE;

    public static final Map<String, String> env = System.getenv();
    public static String POSTHOG_API_KEY = env.get("POSTHOG_API_KEY");

    public Settings() {
    }

    static {
        ARMORY_SITE_URL = dotenv.get("ARMORY_SITE_URL", "");
        MODULE = dotenv.get("MODULE", "local");
    }
}