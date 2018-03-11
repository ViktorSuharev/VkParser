package com.visu.vk.dialog.history;

import com.google.gson.JsonArray;
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
import java.util.List;

public class HistoryManager {

    private static final String USER_ID = "400001943";
    private static final int OFFSET = 0;
    private static final int COUNT = 200;
    private static final boolean REV = false;

    private String lastId;
    private boolean isEnd;

    public List<JsonObject> extractAllDialogHistory(VkApi vkApi) throws Exception {
        List<JsonObject> msgInfo = new ArrayList<>();
        while (!isEnd) {
            extractDialogHistoryBulk(vkApi, msgInfo, lastId);
            Thread.sleep(400L);
        }

        return msgInfo;
    }

    private void extractDialogHistoryBulk(VkApi vkApi, List<JsonObject> msgInfo, String lastMsgId) throws IOException {
        String response = (lastMsgId == null) ?
                vkApi.getHistory(USER_ID, OFFSET, COUNT, REV) :
                vkApi.getHistory(USER_ID, OFFSET, COUNT, lastMsgId);

        JsonArray jMessageItems = new JsonParser()
                .parse(response)
                .getAsJsonObject()
                .getAsJsonObject("response")
                .getAsJsonArray("items");

        for (int i = 0; i < jMessageItems.size(); ++i){
            JsonObject jMessage = jMessageItems.get(i).getAsJsonObject();
            lastMsgId = jMessage.get("id").toString();
            msgInfo.add(jMessage);
        }

        System.out.println("number of msgs " + jMessageItems.size());
        if (jMessageItems.size() < 200) {
            isEnd = true;
        }

        this.lastId = lastMsgId;
        System.out.println(lastMsgId);
    }

    public void writeRecords(List<JsonObject> msgInfo) throws Exception {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("output.txt"), "utf-8"))) {
            Collections.reverse(msgInfo);
            for (JsonObject msgInfoItem : msgInfo) {
                String record = getUserNameById(msgInfoItem.get("from_id").toString())
                        + ": "
                        + msgInfoItem.get("body").toString()
                        + "\n";
                writer.write(record);
            }
        }
    }

    private String getUserNameById(String userId) {
        return (USER_ID.equals(userId)) ? "Collocutor" : "Me";
    }
}
