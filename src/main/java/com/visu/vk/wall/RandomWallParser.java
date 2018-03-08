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
import java.util.HashSet;
import java.util.Iterator;

public class RandomWallParser {

    public static void main(String[] args) throws IOException {
        String APP_ID = "4819849";
        String accessToken = "12863a39703ddef6a5dd13063192dbd26fd5ff7fb9056d4549558abf6d4367a6276490dec6a2e5875e2a6";
        HashSet<String> idSet = new HashSet();

        String responseOfRandomUsers = VkApi.with(APP_ID, accessToken).getRandomUsers();

        JsonElement jRandomUsers = new JsonParser().parse(responseOfRandomUsers);
        JsonObject  jResponseOfRandomUsers = jRandomUsers.getAsJsonObject();
        jResponseOfRandomUsers = jResponseOfRandomUsers.getAsJsonObject("response");
        JsonArray jarrayItemsOfRandomUsers = jResponseOfRandomUsers.getAsJsonArray("items");
        JsonObject jItemOfRandomUsers;
        for (int i = 0; i < jarrayItemsOfRandomUsers.size(); ++i){
            jItemOfRandomUsers = jarrayItemsOfRandomUsers.get(i).getAsJsonObject();
            idSet.add(jItemOfRandomUsers.get("id").toString());
        }

        System.out.println("Size of set is " + idSet.size());
        System.out.println("");

        String userId;
        for (int i = 0; i < jarrayItemsOfRandomUsers.size(); ++i){
            jItemOfRandomUsers = jarrayItemsOfRandomUsers.get(i).getAsJsonObject();
            userId = jItemOfRandomUsers.get("id").toString();

            //delay for 0.3 second
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.out.println("Exception has occured");
            }

            // get friends of random users
            String responseFriends = VkApi.with(APP_ID, accessToken).getUserFriends(userId);

            JsonElement jFriends = new JsonParser().parse(responseFriends);
            JsonObject jResponseOfFriends = jFriends.getAsJsonObject();
            jResponseOfFriends = jResponseOfFriends.getAsJsonObject("response");
            JsonArray jarrayItemsOfFriends = jResponseOfFriends.getAsJsonArray("items");

            if (jarrayItemsOfFriends.size() != 0) {    // May be user have not any friends.
                for (int j = 0; j < jarrayItemsOfFriends.size(); ++j){
                    idSet.add(jarrayItemsOfFriends.get(j).toString());
                }
            }
        }

        System.out.println("Size of set is " + idSet.size());

        try {
            long counter = 0;
            Iterator <String> it = idSet.iterator();
            PrintStream printStream = new PrintStream(new FileOutputStream("output.txt", true), true);
            printStream.println(idSet.size());
            while(it.hasNext()){
                counter++;
                userId = it.next();
                System.out.println("Current user is " + userId);
                printStream.println("Current user is " + userId);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.out.println("Exception has occurred");
                }

                String responseOfWallNotes = VkApi.with(APP_ID, accessToken).getWallByUserId(userId);
                JsonElement jWallNotes = new JsonParser().parse(responseOfWallNotes);
                JsonObject  jResponseOfWallNotes = jWallNotes.getAsJsonObject();
                jResponseOfWallNotes = jResponseOfWallNotes.getAsJsonObject("response");
                if (jResponseOfWallNotes != null) {   // May be user with current ID has deleted profile. In this case vk api sent error response
                    JsonArray jarrayItemsOfWallNotes = jResponseOfWallNotes.getAsJsonArray("items");
                    if (jarrayItemsOfWallNotes.size() != 0) { // May be user have not any notes on the wall.
                        jResponseOfWallNotes = jarrayItemsOfWallNotes.get(0).getAsJsonObject();
                        if (!jResponseOfWallNotes.get("text").toString().equals("\"\"")) {
                            //printStream.println(jResponseOfWallNotes.get("text").toString().substring(1, jResponseOfWallNotes.get("text").toString().length() - 1));
                            System.out.println(counter + " " + jResponseOfWallNotes.get("text").toString());
                            printStream.println(counter + " " + jResponseOfWallNotes.get("text").toString());
                        }
                    }
                }
            }
            //printStream.close();
        }
        // Exception thrown when network timeout occurs
        catch (InterruptedIOException iioe) {
            System.err.println ("Remote host timed out during read operation");
        }
        // Exception thrown when general network I/O error occurs
        catch (IOException ioe) {
            System.err.println ("Network I/O error - " + ioe);
        }

    }

    public static void printSet(HashSet idSet){
        System.out.println("");

        Iterator<String> it = idSet.iterator();
        while(it.hasNext()){
            String element = it.next();
            System.out.println(element);
        }
        System.out.println("Count: " + idSet.size());

    }

    private static void write(String filename, String text) throws FileNotFoundException {
        PrintStream printStream = new PrintStream(new FileOutputStream(filename, true), true);
        printStream.println(text);
        printStream.close();
    }
}
