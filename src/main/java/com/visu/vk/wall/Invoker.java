package com.visu.vk.wall;

import com.visu.vk.VkApi;

public class Invoker {

    private static final String OUTPUT_FILE_NAME = "wall_output.txt";

    public static void main(String[] args) throws Exception {
        VkApi vkApi = VkApi.getDefaultInstance(null);

        RandomWallParser randomWallParser = new RandomWallParser();
        randomWallParser.collect(vkApi, OUTPUT_FILE_NAME);
    }
}
