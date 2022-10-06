package utils;

import com.posthog.java.PostHog;

import java.util.Map;

public class EventTracking {

    private static PostHog instance;
    private static Thread _shutdownHook;

    public static PostHog getInstance() {
        if (instance == null && Settings.POSTHOG_API_KEY != null) { // && BaseSettings.MODULE.equalsIgnoreCase("production")) {
            instance = new PostHog.Builder(Settings.POSTHOG_API_KEY).build();
            _shutdownHook = new Thread(() -> instance.shutdown());
            Runtime.getRuntime().addShutdownHook(_shutdownHook);
        }
        return instance;
    }

    public static void identify(String distinctId, Map<String, Object> properties) {
        if (getInstance() != null)
            getInstance().identify(distinctId, properties);
    }

    public static void identify(String distinctId, Map<String, Object> properties, Map<String, Object> propertiesSetOnce) {
        if (getInstance() != null)
            getInstance().identify(distinctId, properties, propertiesSetOnce);
    }

    public static void capture(String distinctId, String event, Map<String, Object> properties) {
        if (getInstance() != null)
            getInstance().capture(distinctId, event, properties);
    }

    public static void capture(String distinctId, String event) {
        if (getInstance() != null)
            getInstance().capture(distinctId, event);
    }

    public static void alias(String distinctId, String alias) {
        if (getInstance() != null)
            getInstance().alias(distinctId, alias);
    }

    public static void set(String distinctId, Map<String, Object> properties) {
        if (getInstance() != null)
            getInstance().set(distinctId, properties);
    }

    public static void setOnce(String distinctId, Map<String, Object> properties) {
        if (getInstance() != null)
            getInstance().setOnce(distinctId, properties);
    }

    public static void shutdown() {
        if (instance != null) {
            instance.shutdown();
            instance = null;
            Runtime.getRuntime().removeShutdownHook(_shutdownHook);
        }
    }
}
