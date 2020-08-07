package com.example.nightlight;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttRegister extends AsyncTask<String, Void, String> {
    String sendMsg;
    String MQTT_BROKER_IP = Config.MQTT_BROKER;

    @Override
    protected String doInBackground(String... strings) {

        sendMsg = strings[1];
        try
        {
            MqttClient client = new MqttClient(
                    MQTT_BROKER_IP, //URI
                    MqttClient.generateClientId(), //ClientId
                    new MemoryPersistence());
            MqttMessage message = new MqttMessage(sendMsg.getBytes());

            client.connect();
            client.publish(strings[0], message);

            client.disconnect();
        }

        catch (MqttException e) {
            e.printStackTrace();
        } //Persistence

        return "";
    }

}