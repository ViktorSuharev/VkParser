package com.visu.vk;

import com.google.gson.JsonObject;
import com.visu.vk.dialog.history.HistoryManager;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public final class VkApi {

    private static final String API_VERSION = "5.29";

    private static final String AUTH_URL = "https://oauth.vk.com/authorize"
            + "?client_id={APP_ID}"
            + "&scope={PERMISSIONS}"
            + "&redirect_uri={REDIRECT_URI}"
            + "&display={DISPLAY}"
            + "&v={API_VERSION}"
            + "&response_type=token";

    private static final String API_REQUEST = "https://api.vk.com/method/{METHOD_NAME}"
            + "?{PARAMETERS}"
            + "&access_token={ACCESS_TOKEN}"
            + "&v=" + API_VERSION;

    public static VkApi getDefaultInstance(String accessToken) throws IOException {
        String APP_ID = "4819849";
        return VkApi.with(APP_ID, accessToken);
    }

    public static VkApi with(String appId, String accessToken) throws IOException {
        return new VkApi(appId, accessToken);
    }

    private final String accessToken;

    private VkApi(String appId, String accessToken) throws IOException {
        this.accessToken = accessToken;
        if (accessToken == null || accessToken.isEmpty()) {
            auth(appId);
            throw new Error("Need access token");
        }
    }

    private void auth(String appId) throws IOException {
        String reqUrl = AUTH_URL
                .replace("{APP_ID}", appId)
                .replace("{PERMISSIONS}", "photos,messages")
                .replace("{REDIRECT_URI}", "https://oauth.vk.com/blank.html")
                .replace("{DISPLAY}", "page")
                .replace("{API_VERSION}", API_VERSION);
        try {
            Desktop.getDesktop().browse(new URL(reqUrl).toURI());
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public String getDialogs() throws IOException {
        return invokeApi("messages.getDialogs", null);
    }

    public String getPostFromWall() throws IOException {
        return invokeApi("wall.get", null);
    }

    public String getRandomUsers() throws IOException {
        return invokeApi("users.search", Params.create()
                .add("count", "10"));
    }

    public String getUserFriends(String user_id) throws IOException {
        return invokeApi("friends.get", Params.create()
                .add("user_id", user_id)
                .add("fiels", "nickname"));
    }

    public String executeBatchOfMetods(String request) throws IOException {
        return invokeApi("execute", Params.create()
                .add("code", request));
    }

    public String getWallByUserId(String owner_id) throws IOException {
        return invokeApi("wall.get", Params.create()
                .add("owner_id", owner_id));
    }

    public String getHistory(String userId, int offset, int count, boolean rev, String startMessageId) throws IOException {
        return invokeApi("messages.getHistory", Params.create()
                .add("user_id", userId)
                .add("offset", String.valueOf(offset))
                .add("count", String.valueOf(count))
                .add("rev", rev ? "1" : "0")
                .add("start_message_id", startMessageId));
    }

    public String getHistory(String userId, int offset, int count, boolean rev) throws IOException {
        return invokeApi("messages.getHistory", Params.create()
                .add("user_id", userId)
                .add("offset", String.valueOf(offset))
                .add("count", String.valueOf(count))
                .add("rev", rev ? "1" : "0"));
    }

    public String getHistory(String userId, int offset, int count, String startMessageId) throws IOException {
        return invokeApi("messages.getHistory", Params.create()
                .add("user_id", userId)
                .add("offset", String.valueOf(offset))
                .add("count", String.valueOf(count))
                .add("start_message_id", startMessageId));
    }

    public String getAlbums(String userId) throws IOException {
        return invokeApi("photos.getAlbums", Params.create()
                .add("owner_id", userId)
                .add("photo_sizes", "1")
                .add("thumb_src", "1"));
    }

    private String invokeApi(String method, Params params) throws IOException {
        final String parameters = (params == null) ? "" : params.build();
        String reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", method)
                .replace("{ACCESS_TOKEN}", accessToken)
                .replace("{PARAMETERS}&", parameters);
        return invokeApi(reqUrl);
    }

    private static String invokeApi(String requestUrl) throws IOException {
        final StringBuilder result = new StringBuilder();
        final URL url = new URL(requestUrl);
        try (InputStream is = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            reader.lines().forEach(result::append);
        }
        return result.toString();
    }

    private static class Params {

        public static Params create() {
            return new Params();
        }

        private final HashMap<String, String> params;

        private Params() {
            params = new HashMap<>();
        }

        public Params add(String key, String value) {
            params.put(key, value);
            return this;
        }

        public String build() {
            if (params.isEmpty()) return "";
            final StringBuilder result = new StringBuilder();
            params.keySet().stream().forEach(key -> {
                result.append(key).append('=').append(params.get(key)).append('&');
            });
            return result.toString();
        }
    }
}