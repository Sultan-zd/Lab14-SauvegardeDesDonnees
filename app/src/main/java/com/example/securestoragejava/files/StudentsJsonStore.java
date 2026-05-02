package com.example.securestoragejava.files;

import android.content.Context;

import com.example.securestoragejava.model.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StudentsJsonStore {

    public static final String FILE_NAME = "students.json";

    private StudentsJsonStore() {}

    public static void save(Context context, List<Student> students) throws Exception {
        String json = toJson(students);
        try (var fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static List<Student> load(Context context) {
        try {
            String json = InternalTextStore.readUtf8(context, FILE_NAME);
            return fromJson(json);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static boolean delete(Context context) {
        return context.deleteFile(FILE_NAME);
    }

    private static String toJson(List<Student> students) throws Exception {
        JSONArray arr = new JSONArray();
        for (Student s : students) {
            JSONObject obj = new JSONObject();
            obj.put("id", s.id);
            obj.put("name", s.name);
            obj.put("age", s.age);
            arr.put(obj);
        }
        return arr.toString();
    }

    private static List<Student> fromJson(String json) throws Exception {
        JSONArray arr = new JSONArray(json);
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            list.add(new Student(obj.getInt("id"), obj.getString("name"), obj.getInt("age")));
        }
        return list;
    }
}