package com.visu.vk.dialog.history;

import com.visu.vk.VkApi;

public class Invoker {
    public static void main(String[] args) throws Exception {
        String APP_ID = "4819849";
        String accessToken = "12863a39703ddef6a5dd13063192dbd26fd5ff7fb9056d4549558abf6d4367a6276490dec6a2e5875e2a6";

        VkApi vkApi = VkApi.with(APP_ID, accessToken);

        HistoryManager historyManager = new HistoryManager();
        historyManager.extracAllDialogHistory(vkApi);
    }
}
