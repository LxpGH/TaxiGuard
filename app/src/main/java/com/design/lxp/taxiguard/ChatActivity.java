package com.design.lxp.taxiguard;

import android.content.Intent;
import android.graphics.*;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.design.lxp.taxiguard.tools.BitMap;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Msg> msgList = new ArrayList<>();
    private List<HeadImage> imgList = new ArrayList<>();
    private EditText snd_txt;
    private Button send;
    private RecyclerView chat_body;
    private MsgAdapter msgAdapter;
    private Runnable update;
    private Runnable refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initHeadIamge();
        initMsg();
        chat_body = (RecyclerView) findViewById(R.id.chat_body);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chat_body.setLayoutManager(layoutManager);
        msgAdapter = new MsgAdapter(msgList, imgList);
        chat_body.setAdapter(msgAdapter);
        snd_txt = (EditText) findViewById(R.id.snd_msg);
        send = (Button) findViewById(R.id.send_btn);
        send.setOnClickListener(this);

    }

    public void initMsg() {
        Msg msg1 = new Msg(Msg.TYPE_RECEIVED, "吃饭了嘛");
        msgList.add(msg1);
        Msg msg2 = new Msg(Msg.TYPE_SENT, "还没呢,你了");
        msgList.add(msg2);
        Msg msg3 = new Msg(Msg.TYPE_RECEIVED, "也没呢，一起那~");
        msgList.add(msg3);
    }

    public void initHeadIamge() {
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.xiaolan1);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.xiaohong);
        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.xiaolan);
        BitMap bm_tool=new BitMap();
        Bitmap xiaolan1_bm = bm_tool.getOvalBitmap(bitmap1);
        Bitmap xiaohong_bm = bm_tool.getOvalBitmap(bitmap2);
        Bitmap xiaolan_bm = bm_tool.getOvalBitmap(bitmap3);
        HeadImage hd_img1 = new HeadImage(Msg.TYPE_RECEIVED, xiaolan1_bm);
        imgList.add(hd_img1);
        HeadImage hd_img2 = new HeadImage(Msg.TYPE_RECEIVED, xiaohong_bm);
        imgList.add(hd_img2);
        HeadImage hd_img3 = new HeadImage(Msg.TYPE_RECEIVED, xiaolan_bm);
        imgList.add(hd_img3);
    }
    /**获取前一个页面的当前用户**/
    public String  getNowUser(){
    Intent chatIntent=getIntent();
    String cUser=chatIntent.getStringExtra("tab_user");
    String cUser1=chatIntent.getStringExtra("evulateTotab_user");
    if(cUser1==null){
        return cUser;//直接登录传进来的用户
    }else {
        return cUser1;//跳出标签主页面又返回时传进来的用户
    }
         }
    /**获取前一个页面的当前用户**/

    /**获取聊天消息**/
    public void getMessage(){
    String cUserStr=getNowUser();
    ChatPost(cUserStr);
    update=new Runnable() {
        @Override
        public void run() {
            
        }
    }      ;
    }
    /**获取聊天消息**/

    private String msgStr;
    /**聊天界面服务器post异步请求**/
    public void ChatPost(String cUserStr){//cUserStr聊天窗口下当前用户
        OkHttpClient client=new OkHttpClient();
        FormBody.Builder formBody=new FormBody.Builder();
        formBody.add("cUser",cUserStr);
        Request request=new Request.Builder()
                        .url("http://192.168.191.1:8080/chat")
                        .post(formBody.build())
                        .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("myLog:","--------请求失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("myLog:","--------请求成功!");
                if(response.isSuccessful()){
                    msgStr=response.body().string();//将值传回ChatActivity
                }
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_btn) {
            String send_content = String.valueOf(snd_txt.getText());
            Msg msg = new Msg(Msg.TYPE_SENT, send_content);
            msgList.add(msg);
            Bitmap bm_snd = BitmapFactory.decodeResource(getResources(), R.drawable.xiaolan1);//发送这头像
            Bitmap snd_bm = new BitMap().getOvalBitmap(bm_snd);
            HeadImage headImage=new HeadImage(Msg.TYPE_SENT,snd_bm);
            imgList.add(headImage);
            msgAdapter.notifyItemInserted(msgList.size() - 1);
            chat_body.scrollToPosition(msgList.size() - 1);
            snd_txt.setText("");
        }
    }
}
class MsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Msg> msgList;
    private List<HeadImage>imgList;
    public int img_position;
     static class mViewHolder extends RecyclerView.ViewHolder{
        LinearLayout left_layout;
        LinearLayout right_layout;
        TextView left_msg;
        TextView right_msg;
        ImageView left_img;
        ImageView right_img;
        public mViewHolder(View v) {
            super(v);
            left_layout=(LinearLayout)v.findViewById(R.id.left_layout);
            right_layout=(LinearLayout)v.findViewById(R.id.right_layout);
            left_msg=(TextView) v.findViewById(R.id.left_msg);
            right_msg=(TextView)v.findViewById(R.id.right_msg);
            left_img=(ImageView)v.findViewById(R.id.left_img);
            right_img=(ImageView)v.findViewById(R.id.right_img);
        }
    }
    public MsgAdapter(List<Msg> msgList,List<HeadImage>imgList){
        this.msgList=msgList;
        this.imgList=imgList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.msg_item,viewGroup,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        img_position=position;
        Msg msg=msgList.get(position);
        HeadImage hd_img=imgList.get(img_position);
    //System.out.println(msg.getMsg_type());
    if(msg.getMsg_type()==Msg.TYPE_RECEIVED){
        mViewHolder holder=(mViewHolder)vh;
        holder.left_layout.setVisibility(View.VISIBLE);
        holder.right_layout.setVisibility(View.GONE);
        holder.left_msg.setText(msg.getMsg_content());
        holder.left_img.setImageBitmap(hd_img.getImg_src());
    }else if(msg.getMsg_type()==Msg.TYPE_SENT){
        mViewHolder holder=(mViewHolder)vh;
        holder.right_layout.setVisibility(View.VISIBLE);
        holder.left_layout.setVisibility(View.GONE);
        holder.right_msg.setText(msg.getMsg_content());
        holder.right_img.setImageBitmap(hd_img.getImg_src());
    }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
}
class Msg{//聊天信息实体类
    public static final int  TYPE_RECEIVED=0;
    public static final int  TYPE_SENT=1;
    private int msg_type;
    private String msg_content;
    public Msg(int msg_type,String msg_content){
        this.msg_type=msg_type;
        this.msg_content=msg_content;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public String getMsg_content() {
        return msg_content;
    }
}
class HeadImage {
    private int img_type;
    private Bitmap img_src;
    public HeadImage(int img_type,Bitmap img_src){
        this.img_type=img_type;
        this.img_src=img_src;
    }
    public int getImg_type(){ return img_type; }
    public Bitmap getImg_src(){ return img_src; }
}

