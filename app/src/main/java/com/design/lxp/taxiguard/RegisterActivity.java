package com.design.lxp.taxiguard;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText rg_user,rg_pwd,re_pwd;
    private Button btn_regst;
    private Bundle rg_Bundle;
    private Runnable rg_update,rg_sleep;//用于注册页面的视图更新
    boolean  isSuccess=false;//y用于判断是否注册成功
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ImageView imageView=findViewById(R.id.reimg);
        imageView.setOnClickListener(this);
        initView();
    }
    public void initView(){
        rg_user=(EditText)findViewById(R.id.rg_user);
        rg_pwd=(EditText)findViewById(R.id.rg_pwd);
        re_pwd=(EditText)findViewById(R.id.re_pwd);
        btn_regst=findViewById(R.id.btn_regst);

        rg_user.setOnClickListener(this);
        rg_pwd.setOnClickListener(this);
        re_pwd.setOnClickListener(this);
        btn_regst.setOnClickListener(this);
    }
    public void RegisterPost(final String rg_user,final String rg_pwd){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client =new OkHttpClient();
                    //定义传递参数为Json
                    MediaType JSON=MediaType.parse("application/json;charset=utf-8");
                    String jsonStr="{\"user_name\":\""+rg_user+"\",\"pwd\":\""+rg_pwd+"\"}";
                    RequestBody requestBody=RequestBody.create(JSON,jsonStr);
                    Request request =new Request.Builder()
                            .url("http://192.168.191.1:8080/register")//本地服务器
                            .post(requestBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Log.v("myLog","postJson失败!");
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if(response.isSuccessful()){
                                Log.v("myLog","postJson成功拉!");
                                //Log.v("myLog","response.code:"+response.code());
                                String rspStr=response.body().string();
                                if(Pattern.matches(rspStr,"true")){
                                    isSuccess=true;
                                }
                                else{
                                    isSuccess=false;
                                }
                                /**将从服务器获取到的数据进行json解析**/
                                Log.v("myLog","response.body:"+rspStr);
                                //Log.v("myLog","response.body:"+response.body().string());
                            }
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /******注册输入格式验证函数******/
    public static final String FORMAT_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    public static final String FORMAT_PASSWORD = "^[a-zA-Z0-9]{6,12}$";//(至少6位，不包含特殊字符)
    public boolean rgFormatVerification(String rg_User,String rg_Pwd,String re_Pwd){
        boolean is_rg_user= Pattern.matches(FORMAT_MOBILE,rg_User);
        boolean is_rg_pwd=Pattern.matches(FORMAT_PASSWORD,rg_Pwd);
        boolean is_re_pwd=Pattern.matches(rg_Pwd,re_Pwd);//确认两次输入密码是否一致
        if(is_rg_user&&is_rg_pwd&&is_re_pwd){
            return true;
        }else if(!is_rg_user&&is_rg_pwd){
            btn_regst.setText("注册");
            rg_user.setText("");
            Toast.makeText(RegisterActivity.this,"手机号格式错误,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }else if(is_rg_user&&!is_rg_pwd){
            btn_regst.setText("注册");
            rg_pwd.setText("");
            Toast.makeText(RegisterActivity.this,"密码格式错误,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }else if(is_rg_user&&is_rg_pwd&&!is_re_pwd){
            btn_regst.setText("注册");
            re_pwd.setText("");
            Toast.makeText(RegisterActivity.this,"两次输入的密码不匹配,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            btn_regst.setText("注册");
            rg_user.setText("");
            rg_pwd.setText("");
            re_pwd.setText("");
            Toast.makeText(RegisterActivity.this,"手机号,密码格式不正确,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    /******注册输入格式验证函数******/
/******添加自己为联系人******/

/******添加自己为联系人******/
/******注册输入格式验证函数******/
    /******注册控制函数******/
    final static Handler rg_handler=new Handler();
    public void RegisterControl(){
        btn_regst.setText("注册中...");
        final String rg_User=rg_user.getText().toString();
        final String rg_Pwd=rg_pwd.getText().toString();
        final String re_Pwd=re_pwd.getText().toString();
        if(!rgFormatVerification(rg_User,rg_Pwd,re_Pwd)){
            rg_update=null;
            return;
        }else {
            RegisterPost(rg_User, rg_Pwd);
            /**if(isSuccess){
                addMyself();
            }**/
            rg_update = new Runnable() {
                @Override
                public void run() {
                    if (isSuccess) {
                        Intent rg_intent = new Intent(RegisterActivity.this, SuccessActivity.class);
                        rg_intent.putExtra("new_user",rg_User);
                        rg_intent.putExtra("new_pwd",rg_Pwd);
                        startActivity(rg_intent);
                    }else{
                        Toast.makeText(RegisterActivity.this,"注册失败,请稍候重试!",Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }
    /******注册控制函数******/
    @Override
    public void onClick(View view) {
        /**返回登录界面**/
        if(view.getId()==R.id.reimg){
            Intent regIt=new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(regIt);
        }else if(view.getId()==R.id.btn_regst){
        /**注册控制**/
        //System.out.println("---------------------");
        RegisterControl();
            //rg_handler.post(rg_sleep);
        rg_handler.postDelayed(rg_update,2000);
        }
    }
}
