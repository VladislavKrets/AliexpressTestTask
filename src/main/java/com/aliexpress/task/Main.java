package com.aliexpress.task;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.net.*;
import java.time.Instant;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        String csv = "result.csv";
        int maxFields = 100;
        CSVWriter writer = new CSVWriter(new FileWriter(csv));
        String[] fields = Stream.of(Product.class.getDeclaredFields())
                .map(Field::getName).toArray(String[]::new);
        writer.writeNext(fields);
        Gson gson = new Gson();
/*        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress("192.168.49.1", 8282));*/
        int limit = 50;
        String postback = "";
        for (int i = 0; i < maxFields; i += 40) {
            if (maxFields - i < 40) limit = 20;
            StringBuilder respBody = getJsonData(i, limit, postback);
            String jsonData = respBody.substring(respBody.indexOf("(") + 1, respBody.length() - 2);
            AnswerData answerData = gson.fromJson(jsonData, AnswerData.class);
            postback = answerData.getPostback();
            writeCSVData(writer, answerData);
        }
        writer.close();
    }

    private static StringBuilder getJsonData(int offset,
                                             int limit, String postback) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
                "https://gpsfront.aliexpress.com/getRecommendingResults.do?" +
                        "callback=jQuery18306906079412524295_" + System.currentTimeMillis() +
                        "&widget_id=5547572" +
                        "&platform=pc" +
                        "&limit=" + limit +
                        "&offset=" + offset +
                        "&phase=1" +
                        "&productIds2Top=" +
                        "&postback=" + postback +
                        "&_=" + System.currentTimeMillis()).openConnection();
        httpURLConnection.setRequestMethod("GET");
        StringBuilder respBody = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        reader.lines().forEach(respBody::append);
        reader.close();
        return respBody;
    }

    private static void writeCSVData(CSVWriter writer, AnswerData answerData) {
        Field[] fields = Product.class.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        String[] fieldData;
        for (Product product : answerData.getResults()) {
            fieldData = Stream.of(fields).map(x -> {
                try {
                    return x.get(product).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }).toArray(String[]::new);
            writer.writeNext(fieldData);
        }
    }
}
