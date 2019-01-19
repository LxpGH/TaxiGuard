package com.design.lxp.taxiguard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import okhttp3.*;

import java.io.*;
import java.util.regex.Pattern;

import static com.design.lxp.taxiguard.LoginActivity.FORMAT_MOBILE;

public class EvulateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText lpn;
    private EditText driver_phone;
    private EditText evalute_info;
    private ImageButton photo,record_img;
    private ImageView my_photo;
    private ImageView e_back;
    private EditText value;
    private SeekBar value_bar;
    private Button  submit;
    private boolean isSuccess;
    private Runnable ev_update;
    private String record_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evulate);
        getPermission();
        initView();
        showIamge();
    }
    public void initView(){
    lpn=findViewById(R.id.lpn_et);
    driver_phone=findViewById(R.id.phone_tv);
    evalute_info=findViewById(R.id.evulate_et);
    //photo=findViewById(R.id.photo);
        my_photo=findViewById(R.id.photo);
    record_img=findViewById(R.id.record_img);
    value=findViewById(R.id.value);
    value_bar=findViewById(R.id.value_bar);
    value_bar.setMax(100);
    value_bar.setProgress(0);
    submit=findViewById(R.id.submit);
    e_back=findViewById(R.id.e_back);
    record_user=getIntent().getStringExtra("share_user");

    //photo.setOnClickListener(this);
    record_img.setOnClickListener(this);
    submit.setOnClickListener(this);
    e_back.setOnClickListener(this);
    /**添加拖动监听**/
    value_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean from_user) {
            value.setText(String.valueOf(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { System.out.println("stop: > "+seekBar.getProgress()); }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {System.out.println("stop: > "+seekBar.getProgress());}
    });
    }
    public void EvalutePost(long record_id,String record_user,String record_lpn,String driver_phone,
                            String evalute_info,String record_url,int evil_value){
        OkHttpClient client =new OkHttpClient();
        MediaType JSON=MediaType.parse("application/json;charset=utf-8");
        String jsonStr="{\"record_id\":\""+record_id+"\","+"\"record_user\":\""+record_user+"\","+"\"record_lpn\":\""+record_lpn+"\","
                +"\"driver_phone\":\""+driver_phone+"\","+"\"evalute_info\":\""+evalute_info+"\","
                +"\"record_url\":\""+record_url+"\","+"\"evil_value\":\""+evil_value+"\"}";
        RequestBody requestBody=RequestBody.create(JSON,jsonStr);
        Request request =new Request.Builder()
                .url("http://192.168.191.1:8080/evalute")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("myLog","postJson失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d("myLog","postJson成功!");
                    //Log.d("myLog","response.code:"+response.code());
                    //Log.d("myLog","response.body"+response.body().string());
                    if(response.body().string().equals("true")) {
                        isSuccess=true;
                    } else{isSuccess=false;}
                }
            }
        });
    }
    public static final String FORMAT_LPN="^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}" +
            "[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
    public boolean FormatValidation(String record_lpn,String driver_phone, String evalute_info) {
        boolean is_lpn= Pattern.matches(FORMAT_LPN,record_lpn);
        boolean is_driver_phone=Pattern.matches(FORMAT_MOBILE,driver_phone);
        System.out.println("输入的手机号："+driver_phone);
        boolean is_valEvalute_info=evalute_info.length()>=5;

        if(is_lpn&&is_driver_phone&&is_valEvalute_info){
            return true;
        }else if(!is_lpn&&is_driver_phone&&is_valEvalute_info){
            Toast.makeText(EvulateActivity.this,"车牌号格式有误,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }else if(is_lpn&&!is_driver_phone&&is_valEvalute_info){
            Toast.makeText(EvulateActivity.this,"手机号格式有误,请重新输出入!",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!is_valEvalute_info){
            Toast.makeText(EvulateActivity.this,"评价信息至少5个字符,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            Toast.makeText(EvulateActivity.this,"车牌号,手机号格式错误,请重新输入!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public final static Handler handler=new Handler();
    public void SubmitEvulate(){
        long record_id=0;//临时暂存为0，在服务器进行修改
        String record_lpn=lpn.getText().toString();
        String driver_phone_=driver_phone.getText().toString();
        String evalute_info_=evalute_info.getText().toString();
        String record_url="/storage/1/text.txt" ;//读取文件夹没有实现，以默认路径
        String evil_value_str=value.getText().toString();
        int evil_value;
        if(evil_value_str.equals("")){
            evil_value=0;
        }else { evil_value=Integer.valueOf(value.getText().toString());}//需要添加判空,Integer对象valueOf()方法不允许空值。
        if(!FormatValidation(record_lpn,driver_phone_,evalute_info_)){
            ev_update=null;
            return;
        }else {
            EvalutePost(record_id, record_user, record_lpn, driver_phone_, evalute_info_, record_url, evil_value);
            ev_update = new Runnable() {
                @Override
                public void run() {
                    if(isSuccess){
                    submit.setText("提交成功");
                    Toast.makeText(EvulateActivity.this, "感谢您为出行用户安全做出的贡献!", Toast.LENGTH_SHORT).show();
                    }else{
                        submit.setText("提交失败");
                        Toast.makeText(EvulateActivity.this, "提交评价失败!", Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
    }

    /**打开相册**/
    public void getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            int REQUEST_CODE_CONTACT=101;
            String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
            for(String str:permissions){
                if (this.checkSelfPermission(str) !=PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions,REQUEST_CODE_CONTACT);
                    return;
                }

            }
        }
    }
    public Bitmap openImage(String path){
        Bitmap bitmap=null;
        try {
            BufferedInputStream bis =new BufferedInputStream(new FileInputStream(path));
            bitmap=BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public void showIamge(){
        //获取图片路径
        String path=Environment.getExternalStorageDirectory()+"/1/test.png";
       Log.v("-------------------路径：",path);
        Bitmap my_image=openImage(path);
        my_photo.setImageBitmap(my_image);
    }
    /**打开相册**/
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.submit){
            SubmitEvulate();
            handler.postDelayed(ev_update,1000);
        }else if(view.getId()==R.id.e_back){
            /**返回乘车出行界面**/
            Intent back_ev_intent=new Intent(EvulateActivity.this,TabHostActivity.class).putExtra("evulateTotab_user",record_user);
            startActivity(back_ev_intent);
        }
    }
}

