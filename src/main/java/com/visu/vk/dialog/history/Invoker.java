package com.visu.vk.dialog.history;

import com.visu.vk.VkApi;

public class Invoker {

    private static final String OUTPUT_FILE_NAME = "collocutor_output.txt";
    private static final String COLLOCUTOR_ID = "400001943";

    public static void main(String[] args) throws Exception {
        VkApi vkApi = VkApi.getDefaultInstance(null);

        HistoryManager historyManager = new HistoryManager();
        historyManager.extractAllDialogHistory(vkApi, COLLOCUTOR_ID, OUTPUT_FILE_NAME);
    }
}
