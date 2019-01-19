package com.design.lxp.taxiguard;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Handler;
        import android.os.SystemClock;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.Editable;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.*;
        import okhttp3.*;
        import okhttp3.internal.framed.FrameReader;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText user;
    private EditText pwd;
    private Button login_btn;
    private TextView rg_txt;//注册
    private Runnable update,sleep;
    String rUser_name;
    String rPwd;
    String get_user=null;
    String get_pwd=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        autoLogin();
        fetchUser();
    }
    /**初始化视图并设置事件监听**/
    public void initView(){
        user=(EditText)findViewById(R.id.user);
        pwd=(EditText)findViewById(R.id.pwd_lg);
        login_btn=(Button)findViewById(R.id.btn_login);
        rg_txt=(TextView)findViewById(R.id.rg_text);
        ckb_lg=(CheckBox)findViewById(R.id.ckb_lg);
        share_lg=getSharedPreferences("login",MODE_PRIVATE); //声明文件名与操作模式

        user.setOnClickListener(this);
        pwd.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        rg_txt.setOnClickListener(this);

    }
    /**初始化视图并设置事件监听**/

    /**通过post异步请求方式,提交json实现登录控制功能**/
    public void postJson(final String user_name,final String pwd){
                OkHttpClient client = new OkHttpClient();
                //定义传递参数为Json
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                String jsonStr = "{\"user_name\":\"" + user_name + "\",\"pwd\":\"" + pwd + "\"}";
                RequestBody requestBody = RequestBody.create(JSON, jsonStr);
                Request request = new Request.Builder()
                        .url("http://192.168.191.1:8080/login")//本地服务器
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Log.v("myLog","postJson失败!");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.v("myLog", "postJson成功!");
                            //Log.v("myLog","response.code:"+response.code());
                            String rspStr = response.body().string();
                            /**将从服务器获取到的数据进行json解析**/
                            try {
                                JSONObject jsb = new JSONObject(rspStr);
                                rUser_name = jsb.get("user_name").toString();
                                rPwd = jsb.get("pwd").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Log.v("myLog","response.body:"+response.body().string());
                        }
                    }
                });
    }
    /**通过post同步请求方式,提交json实现登录控制功能**/

    final Handler handler=new Handler();
   public void autoLogin(){
       Intent rec_sc=getIntent();
       get_user=rec_sc.getStringExtra("sc_user");
       get_pwd=rec_sc.getStringExtra("sc_pwd");
           user.setText(get_user);
           pwd.setText(get_pwd);
   }
    /**登录控制函数**/
    public void loginControl() {
        /**从页面获取输入**/

        //String _userStr="1";
        //String _pwdStr="2";
        login_btn.setText("登录中...");
        final String userStr = String.valueOf(user.getText());
        final String pwdStr = String.valueOf(pwd.getText());
        if(!formatVerification(userStr,pwdStr)){/**格式不正确,无需连接服务器，直接重新输入**/
            update=null;
            return;
        }else {/**格式正确则提交帐号密码至服务器,验证帐号密码是否匹配**/
            postJson(userStr, pwdStr);
            sleep=new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,"请稍等",Toast.LENGTH_SHORT).show();
                }
            };
            update = new Runnable() {
                @Override
                public void run() {
                    System.out.println(rUser_name + " " + rPwd);
                    if (userStr.equals(rUser_name) && pwdStr.equals(rPwd)) {//登录控制
                        Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();
                        /**记住账号密码**/
                        saveUser(userStr,pwdStr);
                        Intent intent = new Intent(LoginActivity.this, TabHostActivity.class);
                        intent.putExtra("login_user",rUser_name);
                        startActivity(intent);
                    } else {
                        //帐号密码错误，清空输入
                        login_btn.setText("登录");
                        user.setText("");
                        pwd.setText("");
                        Toast.makeText(LoginActivity.this, "帐号或密码错误，请重新输入!", Toast.LENGTH_SHORT).show();
                    }
                    ///handler.postDelayed(update,1000);//每隔一秒循环不断提交UI更新
                }
            };
        }/**格式正确则提交帐号密码至服务器,验证帐号密码是否匹配**/
    }
    /**登录控制函数**/

    /**记住帐号密码功能**/
    private CheckBox ckb_lg;
    private SharedPreferences share_lg;
    public void saveUser(String user,String pwd){
        if(ckb_lg.isChecked()){
            SharedPreferences.Editor editor=share_lg.edit();
            editor.putString("user_name",user);
            editor.putString("pwd",pwd);
            editor.putBoolean("is_checked",true);//同时记住复选框状态
            editor.commit();
        }else{
            return ;
        }

    }
    /**取出帐号密码**/
    public void fetchUser(){
        user.setText(share_lg.getString("user_name",""));
        Log.v("------","user="+share_lg.getString("user_name",""));
        pwd.setText(share_lg.getString("pwd",""));
        Log.v("------","pwd="+share_lg.getString("pwd",""));
        ckb_lg.setChecked(share_lg.getBoolean("is_checked",false));
    }

    /**记住帐号密码功能**/


    /**输入格式验证**/
    public static final String FORMAT_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
    public static final String FORMAT_PASSWORD = "^[a-zA-Z0-9]{6,12}$";//(至少6位，不包含特殊字符)
    public boolean formatVerification(String userStr,String pwdStr){

        /**手机号格式验证**/
        boolean is_user=userStr.length()>=11&&Pattern.matches(FORMAT_MOBILE,userStr);
        boolean is_pwd=Pattern.matches(FORMAT_PASSWORD,pwdStr);
        if(is_user&&is_pwd){
            return true;
        }else{
            if(!is_user&&is_pwd){
                login_btn.setText("登录");
            user.setText("");
            Toast.makeText(LoginActivity.this,"手机号格式不正确,请重新输入!",Toast.LENGTH_SHORT).show();
            }else if(is_user&&!is_pwd){
                login_btn.setText("登录");
                pwd.setText("");
            Toast.makeText(LoginActivity.this,"密码格式不正确,请重新输入!",Toast.LENGTH_SHORT).show();
            }else{
                login_btn.setText("登录");
                user.setText("");
                pwd.setText("");
                Toast.makeText(LoginActivity.this, "手机号,密码格式不正确，请重新输入!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
            /**密码格式验证**/

    }

    /**跳转注册页面**/
    public void intentRegister(){
    Intent rg_intent=new Intent(LoginActivity.this,RegisterActivity.class);
    startActivity(rg_intent);
    }
    /**登录页面返回事件**/
    public void intentBack(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("出行卫士");
        builder.setMessage("确定退出程序?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击确定响应事件
                Intent closeIntent=new Intent();
                closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(closeIntent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {//设置弹窗关闭
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });//dismiss
            }
        });//NeegativeButton
        AlertDialog backAlert=builder.create();
        backAlert.show();
    }

  @Override
  public boolean onKeyDown(int keycode,KeyEvent keyEvent){//手机返回键监听事件
        if(keycode==keyEvent.KEYCODE_BACK){
            intentBack();
            return false;
        }else{
            return super.onKeyDown(keycode,keyEvent);
        }
  }
    //final long[] mHints = new long[4];
    @Override
    public void onClick(View view) {

    if(view.getId()==R.id.btn_login){
        loginControl();
        //handler.postDelayed(sleep,1000);
        handler.postDelayed(update,2000);//设置延迟一秒等待服务器返回数据，当然<一定!>需要设置异步请求
    }else if(view.getId()==R.id.rg_text){
        intentRegister();//点击注册按照跳转到注册页面
    }

    /*******************连续点击实现*******************
        //将mHints数组内的所有元素左移一个位置
        System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
        //获得当前系统已经启动的时间
        mHints[mHints.length - 1] = SystemClock.uptimeMillis();
        if (SystemClock.uptimeMillis() - mHints[0] <= 500) {
            //你的具体操作

        }****************连续点击实现*********************/
       }//onClick

}//LoginActivity
