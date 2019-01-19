package com.design.lxp.taxiguard;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SuccessActivity extends AppCompatActivity implements View.OnClickListener {
    private String new_user;
    private String new_pwd;
    private Button btn_go;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        initView();
        /*****页面间传值例子*****
        Intent sc_intent=getIntent();
        new_pwd =sc_intent.getStringExtra("new_pwd");
        new_user=sc_intent.getStringExtra("new_user");
        System.out.println(new_user+" "+new_pwd);
            ***页面间传值例子*******/
    }
    public void initView(){
        btn_go=findViewById(R.id.btn_go);

        btn_go.setOnClickListener(this);
    }
/*****拨号功能调用例子例子*****
    public void call(String phone){
    Intent call_intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
    <call_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);>去除则直接拨号
    startActivity(call_intent);
}   ****拨号功能调用例子********/
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_go){
        //call("10086");
            Intent sc_intent=getIntent();
            new_pwd =sc_intent.getStringExtra("new_pwd");
            new_user=sc_intent.getStringExtra("new_user");
            Intent go_login=new Intent(SuccessActivity.this,LoginActivity.class);
            go_login.putExtra("sc_user",new_user);
            go_login.putExtra("sc_pwd",new_pwd);
            startActivity(go_login);
        }
    }
}
