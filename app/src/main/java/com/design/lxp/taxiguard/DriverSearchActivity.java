package com.design.lxp.taxiguard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.design.lxp.taxiguard.EvulateActivity.FORMAT_LPN;

public class DriverSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText lpn_ds;//车牌号输入框
    private Button lpn_sh;//查询按纽
    private RecyclerView result_body;//查询结果
    private Runnable ds_update;
    private String resultStr;
    private  List<Driver> mdriverList;
    private DriverAdapter driverAdapter;
    private Driver mdriver;
    private String toDriJson=null;
    public void initView(){
        lpn_ds=this.findViewById(R.id.lpn_ds);
        lpn_sh=this.findViewById(R.id.lpn_sh);
        result_body=this.findViewById(R.id.result_body);
        lpn_sh.setOnClickListener(this);
        }

    public void initDriver() {
        mdriverList=new ArrayList<>();
        mdriver=new Driver("",R.drawable.white);
        mdriverList.add(mdriver);
       /** try {
            JSONArray rs_jsa=new JSONArray(resultStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }**/
    }
    public void initResult(){
        try {
            JSONArray rs_jsa=new JSONArray(resultStr);
            int rsLegenth=rs_jsa.length();
            for(int i=0;i<rsLegenth;i++){
                JSONObject jsb=rs_jsa.getJSONObject(i);
                if(!mdriverList.get(0).getD_user().equals("")){/****如果一开始没有数据需要做出判断，记得对返回来的数据进行判空***/
                    mdriver.setD_user(jsb.getString("record_user"));
                    mdriver.setD_color(R.drawable.white);
                    mdriverList.add(mdriver);
                    driverAdapter.notifyItemInserted(mdriverList.size()-1);
                }else{
                    mdriver.setD_user(jsb.getString("record_user"));
                    mdriver.setD_color(R.drawable.white);
                    mdriverList.set(0,mdriver);
                    driverAdapter.notifyItemChanged(mdriverList.size()-1);
                }
                toDriJson="{"+"\"record_lpn\":\""+jsb.getString("record_lpn")+"\","+"\"driver_phone\":\""+jsb.getString("driver_phone")
                        +"\","+"\"evil_value\":\""+jsb.getString("evil_value")+"\","+"\"evalute_info\":\""+jsb.getString("evalute_info")+"\"}";
                driverAdapter.setOnIntemClickListener(new DriverAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        Intent ds_intent=new Intent(DriverSearchActivity.this,DriverInfoActivity.class).putExtra("toDriJson",toDriJson);
                    startActivity(ds_intent);
                    }
                });
            }
            /**需要将该变量清空，当查询车牌号数据库不存在时，服务器不会返回任何数据，因此需要手动清空，当查询有数据时重新赋值**/
            resultStr=null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public boolean dsFormatValidation(String lpnStr){//仅需判断车牌号是否
        boolean is_vLpn= Pattern.matches(FORMAT_LPN,lpnStr);
        if(is_vLpn) {
            return true;
        }else{
            Toast.makeText(DriverSearchActivity.this,"车牌号格式错误，请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    public void lpnPost(String lpnStr){
        OkHttpClient client=new OkHttpClient();
        FormBody.Builder fb=new FormBody.Builder();
        fb.add("record_lpn",lpnStr);//FormBody对象添加表单数据
        Request request=new Request.Builder()
                .url("http://192.168.191.1:8080/searchDriver")
                .post(fb.build())//post(RequestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d("myLog","post成功!");
                    //Log.d("myLog","response.code:"+response.code());
                    //Log.d("myLog","response.body"+response.body());
                    resultStr=response.body().string();
                }
            }
        });
    }
    /***查询司机信息控制函数***/
    public static final Handler handler=new Handler();
    public void searchDriver(){
    String lpnStr=lpn_ds.getText().toString();
    lpn_sh.setText("查询中...");
    if(!dsFormatValidation(lpnStr)){
        lpn_sh.setText("查询中");
        ds_update=null;
        return ;
    }else {
        lpnPost(lpnStr);
        ds_update = new Runnable() {
            @Override
            public void run() {
                if (resultStr==null) {
                    Toast.makeText(DriverSearchActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                    lpn_sh.setText("查询中");
                    mdriver.setD_user("抱歉,该车牌号车主还没有用户评价");
                    mdriver.setD_color(R.drawable.white);
                    mdriverList.set(0, mdriver);
                    driverAdapter.notifyItemChanged(mdriverList.size() - 1);
                } else {
                    initResult();/**将结果反映到结果界面**/
                    Toast.makeText(DriverSearchActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                    lpn_sh.setText("查询中");
                }
            }
        };
    }
    }
    /***查询司机信息控制函数***/
/***当输入其他车牌号查询时，需清空还原之前的结果列表***/
    public void clear_result(){
        mdriverList.clear();//先清空查询数据，再赋初值
        mdriver.setD_user("");
        mdriver.setD_color(R.drawable.white);
        mdriverList.add(mdriver);
        driverAdapter.notifyDataSetChanged();
    }


    /***当输入其他车牌号查询时，需清空还原之前的结果列表***/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_search);
        initView();
        initDriver();//初始化结果界面
        result_body =(RecyclerView)findViewById(R.id.result_body);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(result_body.getContext());
        result_body.setLayoutManager(layoutManager);
        result_body.addItemDecoration(new  DriverDividerItemDecoration(result_body.getContext()));
        driverAdapter=new DriverAdapter(mdriverList);
        result_body.setAdapter(driverAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.lpn_sh){
            clear_result();
            searchDriver();
            handler.postDelayed(ds_update,1000);//更新UI线程
        }
    }
}


class Driver{
    private String d_user;
    private int d_color;
    public Driver(String d_user,int d_color){
        this.d_user=d_user;
        this.d_color=d_color;
    }

    public void setD_user(String d_user) {
        this.d_user = d_user;
    }

    public String getD_user() {
        return d_user;
    }

    public void setD_color(int d_color) {
        this.d_color = d_color;
    }

    public int getD_color() {
        return d_color;
    }
}
class DriverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Driver> driverList;

    private OnItemClickListener mOnIntemClickListener;
    public DriverAdapter(List<Driver> driverList){this.driverList=driverList;

    }
    public interface OnItemClickListener{
        void OnItemClick(View view ,int position);
    }
    public void setOnIntemClickListener (OnItemClickListener mOnIntemClickListener){
        this.mOnIntemClickListener=mOnIntemClickListener;
    }
    static class InfoViewHolder extends RecyclerView.ViewHolder{
        TextView eUser;
        Button look;
        public InfoViewHolder(View v) {
            super(v);
            eUser=(TextView)v.findViewById(R.id.eUser_txt);
            look=(Button)v.findViewById(R.id.look_btn);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.driver_item,viewGroup,false);
        //填充并获取循环列表视图
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        Driver driver=driverList.get(position);
        final InfoViewHolder driverViewHolder=(InfoViewHolder)vh;
        driverViewHolder.eUser.setText(driver.getD_user());
        //driverViewHolder.look.setBackgroundColor(R.drawable.blue);
        if(mOnIntemClickListener!=null){
            driverViewHolder.look.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    int pos =driverViewHolder.getLayoutPosition();
                    mOnIntemClickListener.OnItemClick(driverViewHolder.look,pos);
                }
            });
        }
        /**奇数蓝色，偶数橙色**/
        if(position%2==0){
            driverViewHolder.look.setBackgroundColor(R.drawable.blue);
        }else{
            driverViewHolder.look.setBackgroundColor(R.drawable.orange);
        }
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

}
class DriverDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable lineDivder;
    public DriverDividerItemDecoration(Context context){
        lineDivder=context.getResources().getDrawable(R.drawable.line_divder);
    }
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state){
        int left=parent.getPaddingLeft();
        int right=parent.getWidth()-parent.getPaddingRight();
        int childCount=parent.getChildCount();
        for(int i=0;i<childCount;i++){
            View child=parent.getChildAt(i);
            RecyclerView.LayoutParams params=(RecyclerView.LayoutParams)child.getLayoutParams();
            int top=child.getBottom()+params.bottomMargin;
            int bottom=top+lineDivder.getIntrinsicHeight();
            lineDivder.setBounds(left,top,right,bottom);
            lineDivder.draw(c);
        }
    }
}