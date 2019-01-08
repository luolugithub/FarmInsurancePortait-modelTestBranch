package com.innovation.login;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by biao on 21/11/2017.
 */

public class RequestTest {

    public static void main(String[] args) throws IOException {
        TreeMap query = new TreeMap<String, String>();
        query.put("mobile", "18601120302");
//        query.put("debug", "YOuXiNPaIsEcReT");
        query.put("xx", "asdfasdf");
        String sn = Utils.createSN(query, "YOuXiNPaIsEcReT");
        System.out.println("sn:" + sn);
        query.put("sn", sn);
        Gson gson = new Gson();
        System.out.println(gson.toJson(query));
        RequestBody formBody = new FormBody.Builder().add("data", gson.toJson(query)).build();
        String response = Utils.post(Utils.LOGIN_URL, formBody);
        System.out.println("response:"+response);

//        try {
//            JSONObject json = JSONObject.fromObject(response);
//            String dd = "{'data':'asd'}";
//            JSONObject jsonObj = new JSONObject("{'data':'asd'}");
//
////            System.out.print(jsonObj.get("mobile"));
//        } catch (Exception e) {
//            System.out.println(e.toString());
//            e.printStackTrace();
//        }

    }

}