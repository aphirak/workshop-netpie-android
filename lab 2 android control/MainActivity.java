package com.myname.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.netpie.microgear.Microgear;
import io.netpie.microgear.MicrogearEventListener;

public class MainActivity extends AppCompatActivity {
    private Microgear microgear = new Microgear(this);
    private String appid = "appid"; //APP_ID
    private String key = "key"; //KEY
    private String secret = "secret"; //SECRET
    private String alias = "android";
    private TextView TextView_humid;
    private TextView TextView_temp;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String humid = bundle.getString("humid");
            String temp = bundle.getString("temp");
            TextView_humid = (TextView) findViewById(R.id.TextView_humid);
            TextView_humid.setText(" " + humid);
            TextView_temp = (TextView) findViewById(R.id.TextView_temp);
            TextView_temp.setText(" " + temp);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MicrogearCallBack callback = new MicrogearCallBack();
        microgear.setCallback(callback);
        microgear.connect(appid, key, secret, alias);
        //microgear.subscribe("/dht");
        microgear.subscribe("/pielinksensor/data");
    }

    protected void onDestroy() {
        super.onDestroy();
        microgear.disconnect();
    }

    protected void onResume() {
        super.onResume();
        microgear.bindServiceResume();
    }

    class MicrogearCallBack implements MicrogearEventListener {
        @Override
        public void onConnect() {
            Log.i("Connected", "Now I'm connected with netpie");
        }

        @Override
        public void onMessage(String topic, String message) {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            String[] message_spit = message.split(",");
            bundle.putString("humid", message_spit[0]);
            bundle.putString("temp", message_spit[1]);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("Message", topic + " : " + message);
        }

        @Override
        public void onPresent(String token) {
            Log.i("present", "New friend Connect :" + token);
        }

        @Override
        public void onAbsent(String token) {
            Log.i("absent", "Friend lost :" + token);
        }

        @Override
        public void onDisconnect() {
            Log.i("disconnect", "Disconnected");
        }

        @Override
        public void onError(String error) {
            Log.i("error", "Error : " + error);
        }

        @Override
        public void onInfo(String info) {
            Log.i("info", "Info : " + info);
        }
    }
}