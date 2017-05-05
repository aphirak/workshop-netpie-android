package com.myname.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import io.netpie.microgear.Microgear;
import io.netpie.microgear.MicrogearEventListener;

public class MainActivity extends AppCompatActivity {

    private Microgear microgear = new Microgear(this);
    private String appid = "appid"; //APP_ID
    private String key = "key"; //KEY
    private String secret = "sercret"; //SECRET
    private String alias = "android";
    private TextView myTextView;
    private Button button_on;
    private Button button_off;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("state");
            myTextView = (TextView)findViewById(R.id.state);
            myTextView.setText(" "+string);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MicrogearCallBack callback = new MicrogearCallBack();
        microgear.resettoken();
        microgear.setCallback(callback);
        microgear.connect(appid,key,secret,alias);
        microgear.subscribe("/pieled/state");

        button_on = (Button) findViewById(R.id.button_on);
        button_on.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                microgear.chat("pieled","1");
            }
        });

        button_off = (Button) findViewById(R.id.button_off);
        button_off.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                microgear.chat("pieled","0");
            }
        });
    }


    protected void onDestroy() {
        super.onDestroy();
        microgear.disconnect();
    }

    protected void onResume() {
        super.onResume();
        microgear.bindServiceResume();
    }

    class MicrogearCallBack implements MicrogearEventListener{
        @Override
        public void onConnect() {
            Log.i("Connected","Now I'm connected with netpie");
        }

        @Override
        public void onMessage(String topic, String message) {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            if(message.equals("1")){
                bundle.putString("state", "ON");
            }else{
                bundle.putString("state", "OFF");
            }
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("Message",topic+" : "+message);
        }

        @Override
        public void onPresent(String token) {
            Log.i("present","New friend Connect :"+token);
        }

        @Override
        public void onAbsent(String token) {
            Log.i("absent","Friend lost :"+token);
        }

        @Override
        public void onDisconnect() {
            Log.i("disconnect","Disconnected");
        }

        @Override
        public void onError(String error) {
            Log.i("error","Error : "+error);
        }

        @Override
        public void onInfo(String info) {
            Log.i("info","Info : "+info);
        }
    }
}
