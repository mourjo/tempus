package me.mourjo.tempus.utils;

public class Environment {

    public static final int DEFAULT_PORT = 8112;
    private static final String ENV_PORT = "PORT";
    private static final String ENV_API_KEY = "API_KEY";

    public static int port() {
        try {
            String port = System.getenv(ENV_PORT);
            if (port != null) {
                return Integer.parseInt(port);
            }
        } catch (Exception ignored) {

        }
        return DEFAULT_PORT;
    }

    public static String apiKey() {
        return System.getenv(ENV_API_KEY);
    }
}
