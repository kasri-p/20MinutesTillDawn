package com.untilDawn;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class SSLTest {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://cluster0.iasve1x.mongodb.net");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            System.out.println("SSL connection successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
