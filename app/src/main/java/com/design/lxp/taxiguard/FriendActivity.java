package com.design.lxp.taxiguard;

import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {
    //private ListView listview;
    private Adapter adapter;
    private List <String> data;
    private SearchView sv;
    private ExpandableListView fExpView;
    private Runnable fr_update,fr_sleep;
    public List<String> groupList;
    public List<List<String>> childList ;
    //List<String> myselfList=new ArrayList<>();
    List<String> contactList=new ArrayList<>();
    List<String> driverList=new ArrayList<>();
    List<String> friendList=new ArrayList<>();
    public void initView() {
        groupList = new ArrayList<>();
        groupList.add("常用联系人");
        groupList.add("信任车主");
        groupList.add("好友");

        contactList.add("");
        driverList.add("");
        friendList.add("");
        /**初始化自列表数据**/
        childList = new ArrayList<>();
        /**拖拽监听*******
         fExpView.setOnDragListener(new View.OnDragListener() {
        @Override public boolean onDrag(View view, DragEvent dragEvent) {
        final int action=dragEvent.getAction();
        switch(action){
        case DragEvent.ACTION_DRAG_STARTED: // 拖拽开始
        return dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        case DragEvent.ACTION_DRAG_ENTERED: // 被拖拽View进入目标区域
        return true;
        case DragEvent.ACTION_DRAG_LOCATION: // 被拖拽View在目标区域移动
        return true;
        case DragEvent.ACTION_DRAG_EXITED: // 被拖拽View离开目标区域
        return true;
        case DragEvent.ACTION_DROP: // 放开被拖拽View
        String content = dragEvent.getClipData().getItemAt(0).getText().toString(); //接收数据
        Toast.makeText(FriendActivity.this,content,Toast.LENGTH_SHORT).show();
        return true;
        case DragEvent.ACTION_DRAG_ENDED:
        return true;// 拖拽完成
        }
        return false;
        }
        });***/
    }
    public void getFriends() {
        Intent frIntent=getIntent();
        String fr_user;
        String fr_user_=frIntent.getStringExtra("tab_user");
        String fr_user1=frIntent.getStringExtra("evulateTotab_user");
        if(fr_user_!=null)
        {
            fr_user=fr_user_;
        }else {
            fr_user=fr_user1;
        }
        System.out.println("可以接收到当前用户id:"+fr_user);//输出:测试是否正确接收到当前用户id
        /**根据用户id查询其向服务器发送请求获取联系人列表**/
        FriendsPost(fr_user);
        //System.out.println("----------"+childList.get(0).toString());
        newFriends();
    }

/***用于更新联系人视图函数***/
public static final Handler frHandler=new Handler();
private void newFriends(){
    fr_update=new Runnable() {
        @Override
        public void run() {
            childList.add(contactList);
            childList.add(driverList);
            childList.add(friendList);
            friendExpandAdapter fExpAdapter =new friendExpandAdapter(groupList,childList);
            fExpView.setAdapter(fExpAdapter);
        }
    };
}
/***用于更新联系人视图函数***/

/***通过post异步请求获取联系人列表****/
    private void FriendsPost(String fr_user) {//请求值在类内部访问，设置private
        OkHttpClient client =new OkHttpClient();
        FormBody.Builder fb=new FormBody.Builder();
        System.out.println("传给服务器id:"+fr_user);
        fb.add("rof_user",fr_user);//FormBody对象添加表单数据
        Request request=new Request.Builder()
                .url("http://192.168.191.1:8080/getFriends")
                .post(fb.build())//post(RequestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.d("myLog","post成功!");
                    // Log.d("myLog","response.code:"+response.code());
                    //Log.d("myLog","response.body"+response.body());
                    String rspStr=response.body().string();
                    try {//有可能字符串并不是标准JSONArray格式
                        JSONArray jsonArray=new JSONArray(rspStr);
                        int Datacount=jsonArray.length();

                        for(int i=0;i<Datacount;i++){//遍历JSONArray字符串
                            JSONObject friend=jsonArray.getJSONObject(i);
                            String friendTypeStr=friend.get("relation_type").toString();
                            String friendName=friend.get("remark_name").toString();
                            Integer friendType =Integer.valueOf(friendTypeStr);
                            if(friendType.equals(1)){
                                if(contactList.get(0).equals("")){
                                    contactList.set(0,friendName);
                                }else{
                                    contactList.add(friendName);
                                }
                            }else if(friendType.equals(2)){
                                if(driverList.get(0).equals("")){
                                    driverList.set(0,friendName);
                                }else{
                                    driverList.add(friendName);
                                }
                            }else if(friendType.equals(3)){
                                if(friendList.get(0).equals("")){
                                    friendList.set(0,friendName);
                                }else{
                                     friendList.add(friendName);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /**返回的数据应该是所有匹配的relation**/

                }
            }
        });
    }
    /***通过post异步请求获取联系人列表****/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        fExpView= (ExpandableListView)findViewById(R.id.friendView);
        initView();
        getFriends();
        //frHandler.post(fr_sleep);
        frHandler.postDelayed(fr_update,2000);
        //friendExpandAdapter fExpAdapter=new friendExpandAdapter();
        //fExpView.setAdapter(fExpAdapter);
        /**ListView初始化**/
        /**
        getData();
        listview=(ListView)findViewById(R.id.friends);
        adapter=new ArrayAdapter<String>(FriendActivity.this,android.R.layout.simple_list_item_single_choice,data);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter((ListAdapter) adapter);
         }
    public void getData(){
        data=new ArrayList<String>();
        data.add("北京");
        data.add("深圳");
        data.add("上海");
        data.add("重庆");
        data.add("南昌");
    }**/
    /**ListView初始化**/
        //设置分组的监听
        /**
        fExpView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), groupList[groupPosition], Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //设置子项布局监听
        fExpView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), childList[groupPosition][childPosition], Toast.LENGTH_SHORT).show();
                return true;
            }
        });**/
    }
}


 class friendExpandAdapter extends BaseExpandableListAdapter {

     private List<String>groupList;
     private List<List<String>>childList;
     /**private String[] groupList = {"常用联系人", "信任车主", "常用联系人", "好友"};
     public String[][] childList = {{"        孙尚香", "        后羿", "        马可波罗", "       狄仁杰"},
             {"        孙膑", "        蔡文姬", "        鬼谷子", "        杨玉环"},
             {"        张飞", "        廉颇", "        牛魔", "        项羽"},
             {"        诸葛亮", "        干将", "        安其拉"}};**/

     /**构造函数**/
     public  friendExpandAdapter(List<String>groupList,List<List<String>>childList){
      this.groupList=groupList;
      this.childList=childList;
     }

     // 获取分组的个数
     @Override
     public int getGroupCount() {
         return groupList.size();
     }
     //获取指定分组中的子选项的个数
     @Override
     public int getChildrenCount(int groupPosition) {
         return childList.get(groupPosition).size();
     }
     //        获取指定的分组数据
     @Override
     public Object getGroup(int groupPosition) {
         return childList.get(groupPosition);
     }
     //获取指定分组中的指定子选项数据
     @Override
     public Object getChild(int groupPosition, int childPosition) {
         return childList.get(groupPosition).get(childPosition);
     }
     //获取指定分组的ID, 这个ID必须是唯一的
     @Override
     public long getGroupId(int groupPosition) {
         return groupPosition;
     }
     //获取子选项的ID, 这个ID必须是唯一的
     @Override
     public long getChildId(int groupPosition, int childPosition) {
         return childPosition;
     }
     //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
     @Override
     public boolean hasStableIds() {
         return true;
     }

     /**
      *
      * @param groupPosition 组位置
      * @param isExpand 该组是展开状态还是伸缩状态
      * @param convertView 重用已有的视图对象
      * @param viewGroup 返回的视图对象始终依附于的视图组
      * @return
      */
     @Override
     public View getGroupView(int groupPosition, boolean isExpand, View convertView, ViewGroup viewGroup) {
         GroupViewHolder groupViewHolder;
         if (convertView == null){
             convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.parent_item,viewGroup,false);
             groupViewHolder = new GroupViewHolder();
             groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_group_normal);
             convertView.setTag(groupViewHolder);
         }else {
             groupViewHolder = (GroupViewHolder)convertView.getTag();
         }
         groupViewHolder.tvTitle.setText(groupList.get(groupPosition));
         return convertView;
     }
     /**
      *
      * 获取一个视图对象，显示指定组中的指定子元素数据。
      *
      * @param groupPosition 组位置
      * @param childPosition 子元素位置
      * @param isLastChild 子元素是否处于组中的最后一个
      * @param convertView 重用已有的视图(View)对象
      * @param viewGroup 返回的视图(View)对象始终依附于的视图组
      * @return
      * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
      *      android.view.ViewGroup)
      */

     @Override
     public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
         ChildViewHolder childViewHolder;
         if (convertView==null){
             convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.child_item,viewGroup,false);
             childViewHolder = new ChildViewHolder();
             childViewHolder.tvTitle = (TextView)convertView.findViewById(R.id.expand_child);
             convertView.setTag(childViewHolder);

         }else {
             childViewHolder = (ChildViewHolder) convertView.getTag();
         }
         childViewHolder.tvTitle.setText(childList.get(groupPosition).get(childPosition).toString());
         return convertView;
     }

     @Override
     public boolean isChildSelectable(int groupPosition, int childPosition) {
         return true;
     }
     static class GroupViewHolder {
         TextView tvTitle;
     }

     static class ChildViewHolder {
         TextView tvTitle;
     }
 }
