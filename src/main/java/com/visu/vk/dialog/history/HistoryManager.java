package com.visu.vk.dialog.history;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.visu.vk.VkApi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryManager {

    private static final int OFFSET = 0;
    private static final int COUNT = 200;
    private static final boolean REV = false;

    private String lastId;
    private boolean isEnd;

    public List<JsonObject> extractAllDialogHistory(VkApi vkApi, String collocutorId, String output) throws Exception {
        List<JsonObject> msgInfo = new ArrayList<>();
        while (!isEnd) {
            extractDialogHistoryBulk(vkApi, msgInfo, lastId, collocutorId);
            Thread.sleep(400L);
        }

        writeRecords(output, msgInfo, collocutorId);

        return msgInfo;
    }

    private void extractDialogHistoryBulk(VkApi vkApi, List<JsonObject> msgInfo, String lastMsgId, String userId) throws IOException {
        String response = (lastMsgId == null) ?
                vkApi.getHistory(userId, OFFSET, COUNT, REV) :
                vkApi.getHistory(userId, OFFSET, COUNT, lastMsgId);

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

        if (jMessageItems.size() < 200) {
            isEnd = true;
        }

        this.lastId = lastMsgId;
    }

    private void writeRecords(String fileName, List<JsonObject> msgInfo, String collocutorId) throws Exception {
        try (PrintStream printStream = new PrintStream(new FileOutputStream(fileName, true), true)) {
            Collections.reverse(msgInfo);
            msgInfo.forEach((msgInfoItem) -> printStream.println(formatString(msgInfoItem, collocutorId)));
        }
    }

    private String formatString(JsonObject msgInfoItem, String collocutorId) {
        return getUserNameById(msgInfoItem, collocutorId) + msgInfoItem.get("body").toString();
    }

    private String getUserNameById(JsonObject userId, String collocutorId) {
        return (collocutorId.equals(userId.get("from_id").toString())) ? "Collocutor: " : "Me: ";
    }
}
