package com.camerinfolks.controller;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


@Component(value="mobileFireBaseNotification")
public class MobileFireBaseNotification {

	public final static String AUTH_KEY_FCM = "AAAA1w-Fczw:APA91bErDsGYDMO_DKBoIMKf1289DNAVafmmFOiVd1QFLe1s60cgSDI44M1G1aYaSREBsCuwtjc_SyjUkWniL8Lz7cp1gAixwvUT4zwmoOHPtEhVEIvHe7SOCV4CleZ7873p04BejsTf";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public static void pushFCMNotification(String DeviceIdKey,String title,String body) throws Exception {

        String authKey = AUTH_KEY_FCM;
        String FMCurl = API_URL_FCM;

        URL url = new URL(FMCurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + authKey);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject data = new JSONObject();
        data.put("to", DeviceIdKey.trim());
        JSONObject info = new JSONObject();
        info.put("title", title);
        info.put("body", body);
        data.put("data", info);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data.toString());
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

    }
//    public static void pushFCMSendCall(String DeviceIdKey,String title,String body) throws Exception {
//        String authKey = AUTH_KEY_FCM;
//        String FMCurl = API_URL_FCM;
//        URL url = new URL(FMCurl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        conn.setUseCaches(false);
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Authorization", "key=" + authKey);
//        conn.setRequestProperty("Content-Type", "application/json");
//
//        org.json.JSONObject data = new JSONObject();
//        JSONObject dataob = new JSONObject();
//        data.put("to", DeviceIdKey.trim());
//        dataob.put("call",title);
//        dataob.put("channelid",body);
//        data.put("data", dataob);
//        System.out.println("Response : " + data);
//        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//        wr.write(data.toString());
//        wr.flush();
//        wr.close();
//
//        int responseCode = conn.getResponseCode();
//        System.out.println("Response Code : " + responseCode);
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//    }
}
