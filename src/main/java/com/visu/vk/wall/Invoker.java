package com.visu.vk.wall;

import com.visu.vk.VkApi;
public class Invoker {
    public static void main(String[] args) throws Exception {
        VkApi vkApi = VkApi.getDefaultInstance("f26936a5d237d7308999aee98bd1f1a708268b0b4b16a85a7aca2d391a72a3347bb27307293247396f60a");

        RandomWallParser randomWallParser = new RandomWallParser();
        randomWallParser.collect(vkApi);
    }
}
