package com.visu.vk.dialog.history;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visu.vk.VkApi;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryManager {

    private static final String USER_ID = "400001943";
    private static final int OFFSET = 0;
    private static final int COUNT = 200;
    private static final boolean REV = false;

    private final List<JsonObject> msgInfo = new ArrayList<>();
    private final Set<String> messageSet = new HashSet<>();
    private String lastId = "0";
    private boolean isEnd = false;

    public void extracAllDialogHistory(VkApi vkApi) throws Exception {
        extractDialogHistoryFirstBulk(vkApi);
        while (!isEnd) {
            extractDialogHistoryBulk(vkApi, lastId);
            Thread.sleep(400L);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("filename.txt"), "utf-8"))) {
            Collections.reverse(msgInfo);
            for (JsonObject msgInfoItem : msgInfo) {
                writer.write(getUserNameById(msgInfoItem.get("from_id").toString()) + ": " + msgInfoItem.get("body").toString() + "\n");
            }
        }
    }

    public void extractDialogHistoryFirstBulk(VkApi vkApi) throws IOException {
        String response = vkApi.getHistory(USER_ID, OFFSET, COUNT, REV);

        JsonElement jRandomUsers = new JsonParser().parse(response);
        JsonObject jResponseOfRandomUsers = jRandomUsers.getAsJsonObject();
        jResponseOfRandomUsers = jResponseOfRandomUsers.getAsJsonObject("response");
        JsonArray jarrayItemsOfRandomUsers = jResponseOfRandomUsers.getAsJsonArray("items");

        String lastMessageId = "sdf";
        String messageBody;
        String messageAuthor;
        boolean init = true;

        for (int i = 0; i < jarrayItemsOfRandomUsers.size(); ++i){

            JsonObject jItemOfRandomUsers;
            jItemOfRandomUsers = jarrayItemsOfRandomUsers.get(i).getAsJsonObject();
/*            lastMessageId = jItemOfRandomUsers.get("id").toString();
            messageBody = jItemOfRandomUsers.get("body").toString();
            messageAuthor = jItemOfRandomUsers.get("from_id").toString();*/

            messageSet.add(lastMessageId);
//            System.out.println(getUserNameById(messageAuthor) + ": " + messageBody);

            msgInfo.add(jItemOfRandomUsers);

            lastMessageId = jItemOfRandomUsers.get("id").toString();
        }

        System.out.println("number of msgs " + jarrayItemsOfRandomUsers.size());
        if (jarrayItemsOfRandomUsers.size() < 200) {
            isEnd = true;
        }

        this.lastId = lastMessageId;
        System.out.println(lastMessageId);
    }

    public void extractDialogHistoryBulk(VkApi vkApi, String lastMsgId) throws IOException {
//        System.out.println(USER_ID + " " + OFFSET + " " + COUNT + " " + lastMsgId);
        String response = vkApi.getHistory(USER_ID, OFFSET, COUNT, lastMsgId);

        JsonElement jRandomUsers = new JsonParser().parse(response);
        JsonObject jResponseOfRandomUsers = jRandomUsers.getAsJsonObject();

//        System.out.println(jResponseOfRandomUsers.toString());
        jResponseOfRandomUsers = jResponseOfRandomUsers.getAsJsonObject("response");
        JsonArray jarrayItemsOfRandomUsers = jResponseOfRandomUsers.getAsJsonArray("items");

        String lastMessageId = "sdf";
        String messageBody;
        String messageAuthor;
        boolean init = true;

        Set<String> messageSet = new HashSet<>();

        for (int i = 0; i < jarrayItemsOfRandomUsers.size(); ++i){

            JsonObject jItemOfRandomUsers;
            jItemOfRandomUsers = jarrayItemsOfRandomUsers.get(i).getAsJsonObject();
/*            lastMessageId = jItemOfRandomUsers.get("id").toString();
            messageBody = jItemOfRandomUsers.get("body").toString();
            messageAuthor = jItemOfRandomUsers.get("from_id").toString();*/

            messageSet.add(lastMessageId);
//            System.out.println(getUserNameById(messageAuthor) + ": " + messageBody);

            lastMessageId = jItemOfRandomUsers.get("id").toString();
            msgInfo.add(jItemOfRandomUsers);
        }

        System.out.println("number of msgs " + jarrayItemsOfRandomUsers.size());
        if (jarrayItemsOfRandomUsers.size() < 200) {
            isEnd = true;
        }

        this.lastId = lastMessageId;
        System.out.println(lastMessageId);
    }

    private String getUserNameById(String userId) {
        return ("400001943".equals(userId)) ? "Ira" : "Viktor";
    }
}
