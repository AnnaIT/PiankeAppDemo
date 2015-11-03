package com.example.administrator.piankeappdemo.toolsclass;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 董梦娇 on 2015/10/12.
 */
public class GsonUtils {
    public static <T> List<T> readJsonArray(JSONArray array,Class<T> entityType){
        Gson gson = new Gson();
        List<T> list = new ArrayList<>();
        for (int i = 0; i <array.length();i++){//通过gson将解析json
            try {
                T t = gson.fromJson(array.getJSONObject(i).toString(),entityType);
                list.add(t);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
