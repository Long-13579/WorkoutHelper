package com.example.myapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplication.Domain.ChatHistory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHistoryManager {
    private static final String PREF_NAME = "ChatHistoryPrefs";
    private static final String KEY_HISTORY = "chat_history";
    private static final int MAX_HISTORY = 50; // Giới hạn 50 cuộc trò chuyện

    public static void saveChatHistory(Context context, ChatHistory chatHistory) {
        List<ChatHistory> historyList = loadChatHistory(context);
        
        // Thêm vào đầu danh sách
        historyList.add(0, chatHistory);
        
        // Giới hạn số lượng
        if (historyList.size() > MAX_HISTORY) {
            historyList = historyList.subList(0, MAX_HISTORY);
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString(KEY_HISTORY, json);
        editor.apply();
    }

    public static List<ChatHistory> loadChatHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_HISTORY, null);
        
        if (json == null) {
            return new ArrayList<>();
        }
        
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ChatHistory>>(){}.getType();
            List<ChatHistory> historyList = gson.fromJson(json, type);
            return historyList != null ? historyList : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void deleteChatHistory(Context context, String historyId) {
        List<ChatHistory> historyList = loadChatHistory(context);
        historyList.removeIf(history -> history.getId().equals(historyId));
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString(KEY_HISTORY, json);
        editor.apply();
    }

    public static void clearAllHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_HISTORY);
        editor.apply();
    }

    public static ChatHistory getChatHistoryById(Context context, String id) {
        List<ChatHistory> historyList = loadChatHistory(context);
        for (ChatHistory history : historyList) {
            if (history.getId().equals(id)) {
                return history;
            }
        }
        return null;
    }
}

