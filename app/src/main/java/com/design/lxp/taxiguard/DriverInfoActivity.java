package com.design.lxp.taxiguard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class DriverInfoActivity extends AppCompatActivity {
    private TextView lpn_tv;
    private TextView phone_tv;
    private TextView evulate_tv;
    private TextView value_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        lpn_tv=findViewById(R.id.lpn_tv);
        phone_tv=findViewById(R.id.phone_tv);
        evulate_tv=findViewById(R.id.evulate_tv);
        value_info=findViewById(R.id.value_info);
        Intent diIntent=getIntent();
        String dfStr=diIntent.getStringExtra("toDriJson");
        try {
            JSONObject fd_jsb=new JSONObject(dfStr);
            lpn_tv.setText(fd_jsb.getString("record_lpn"));
            phone_tv.setText(fd_jsb.getString("driver_phone"));
            evulate_tv.setText(fd_jsb.getString("evalute_info"));
            value_info.setText(fd_jsb.getString("evil_value"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
