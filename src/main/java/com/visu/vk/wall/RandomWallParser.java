package com.visu.vk.wall;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visu.vk.VkApi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RandomWallParser {

    private static final long DELAY_TIME = 300L;

    private Set<String> idSet;

    public void collect(VkApi vkApi) throws IOException {
        JsonArray jRandomUserItems = collectUserIds(vkApi);
        collectUserFriendIds(vkApi, jRandomUserItems);
        Map<String, String> userIdLastMsgMap = getWallNotes(vkApi);

    }

    private JsonArray collectUserIds(VkApi vkApi) throws IOException {
        String responseOfRandomUsers = vkApi.getRandomUsers();
        JsonArray jRandomUserItems = new JsonParser()
                .parse(responseOfRandomUsers)
                .getAsJsonObject()
                .getAsJsonObject("response")
                .getAsJsonArray("items");

        idSet = new HashSet<>();
        for (JsonElement jsonElement : jRandomUserItems) {
            String userId = jsonElement
                    .getAsJsonObject()
                    .get("id")
                    .toString();
            idSet.add(userId);
        }

        System.out.println("Size of set is " + idSet.size());
        System.out.println("");


        return jRandomUserItems;
    }

    private void collectUserFriendIds(VkApi vkApi, JsonArray jRandomUserItems) throws IOException {
        for (int i = 0; i < jRandomUserItems.size(); ++i){
            String userId = jRandomUserItems
                    .get(i)
                    .getAsJsonObject()
                    .get("id")
                    .toString();

            // get friends of random users
            String userFriends = vkApi.getUserFriends(userId);
            delay(DELAY_TIME);

            JsonArray jFriendItems = new JsonParser()
                    .parse(userFriends)
                    .getAsJsonObject()
                    .getAsJsonObject("response")
                    .getAsJsonArray("items");

            // May be user have not any friends.
            if (jFriendItems.size() != 0) {
                for (int j = 0; j < jFriendItems.size(); ++j){
                    idSet.add(jFriendItems.get(j).toString());
                }
            }
        }

        System.out.println("Size of set is " + idSet.size());
    }

    private Map<String, String> getWallNotes(VkApi vkApi) throws IOException {
        Map<String, String> userIdLastMsgMap = new HashMap<>();
        for (String userId : idSet){
            System.out.println("Current user is " + userId);

            delay(DELAY_TIME);
            String wallNotes = vkApi.getWallByUserId(userId);

            JsonObject jWallNotes = new JsonParser()
                    .parse(wallNotes)
                    .getAsJsonObject()
                    .getAsJsonObject("response");

            if (jWallNotes != null) {   // May be user with current ID has deleted profile. In this case vk api sent error response
                JsonArray jWallNoteItems = jWallNotes.getAsJsonArray("items");
                if (jWallNoteItems.size() != 0) { // May be user have not any notes on the wall.
                    jWallNotes = jWallNoteItems.get(0).getAsJsonObject();
                    if (!jWallNotes.get("text").toString().equals("\"\"")) {
                        String lastWallNote = jWallNotes.get("text").toString();
                        userIdLastMsgMap.put(userId, lastWallNote);
                    }
                }
            }
        }

        return userIdLastMsgMap;
    }

    public static void printMap(Map<String, String> map){
        for (Map.Entry<String, String> entry : map.entrySet()) {
            
        }
    }

    private static void write(String filename, String text) throws FileNotFoundException {
        PrintStream printStream = new PrintStream(new FileOutputStream(filename, true), true);
        printStream.println(text);
        printStream.close();
    }

    private static void delay(long time) {
        //delay for 0.3 second
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
