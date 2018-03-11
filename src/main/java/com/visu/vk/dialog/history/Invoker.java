package com.visu.vk.dialog.history;

import com.google.gson.JsonObject;
import com.visu.vk.VkApi;

import java.util.List;

public class Invoker {
    public static void main(String[] args) throws Exception {
        String APP_ID = "4819849";
        String accessToken = "58e410ba44107f879a5c4eb4f633fe51fdfe1e6791cca063814660a9243b84d5c55362692585610399440";

        VkApi vkApi = VkApi.with(APP_ID, accessToken);

        HistoryManager historyManager = new HistoryManager();
        List<JsonObject> msgInfoList = historyManager.extractAllDialogHistory(vkApi);
        historyManager.writeRecords(msgInfoList);
    }
}
