package com.visu.vk.dialog.history;

import com.google.gson.JsonObject;
import com.visu.vk.VkApi;

import java.util.List;

public class Invoker {
    public static void main(String[] args) throws Exception {
        VkApi vkApi = VkApi.getDefaultInstance(null);

        HistoryManager historyManager = new HistoryManager();
        List<JsonObject> msgInfoList = historyManager.extractAllDialogHistory(vkApi);
        historyManager.writeRecords(msgInfoList);
    }
}
