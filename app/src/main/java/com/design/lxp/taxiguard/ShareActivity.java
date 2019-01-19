package com.design.lxp.taxiguard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ShareActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton evalate_img;
    private ImageButton search_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        evalate_img=findViewById(R.id.evaluate_img);
        evalate_img.setOnClickListener(this);
        search_img=findViewById(R.id.search_img);
        search_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.evaluate_img){
            String share_user=getIntent().getStringExtra("tab_user");
            Intent share_intent=new Intent(ShareActivity.this,EvulateActivity.class);
            share_intent.putExtra("share_user",share_user);
            startActivity(share_intent);
        }else if(view.getId()==R.id.search_img){
            Intent share_intent1=new Intent(ShareActivity.this,DriverSearchActivity.class);
            startActivity(share_intent1);
        }

    }
}
