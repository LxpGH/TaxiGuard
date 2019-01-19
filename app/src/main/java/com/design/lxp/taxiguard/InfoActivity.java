package com.design.lxp.taxiguard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.design.lxp.taxiguard.tools.BitMap;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {
    private List<InfoItem> infoList =new ArrayList<>();
    private RecyclerView info_body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initInfoItem();
        info_body =(RecyclerView)findViewById(R.id.info_body);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(info_body.getContext());
        info_body.setLayoutManager(layoutManager);
        info_body.addItemDecoration(new  mDividerItemDecoration(info_body.getContext()));
        InfoAdapter infoAdapter=new InfoAdapter(infoList);
        info_body.setAdapter(infoAdapter);

    }
    public void initInfoItem(){
        /**用户名栏**/
        Bitmap user_bm=BitmapFactory.decodeResource(getResources(),R.drawable.xiaolan1);
        Bitmap user_icon=new BitMap().getOvalBitmap(user_bm);
        String user_name="昵称";
        int ulist_num=0;
        InfoItem info_user=new InfoItem(ulist_num,user_icon,user_name);
        infoList.add(info_user);
        /**用户名栏**/

        /**性别栏**/
        Bitmap sex_icon=null;
        String sex_name="性别";
        int slist_num=1;
        InfoItem info_sex=new InfoItem(slist_num,sex_icon,sex_name);
        infoList.add(info_sex);
        /**性别栏**/

        /**年龄栏**/
        Bitmap year_icon=null;
        String year_name="年龄";
        int ylist_num=2;
        InfoItem info_year=new InfoItem(ylist_num,year_icon,year_name);
        infoList.add(info_year);
        /**年龄栏**/
        /**实名认证栏**/
        Bitmap real_icon=null;
        String real_name="实名认证";
        int rlist_num=3;
        InfoItem info_real=new InfoItem(rlist_num,real_icon,real_name);
        infoList.add(info_real);
        /**实名认证栏**/

        /**手机号绑定栏**/
        Bitmap phone_icon=null;
        String phone_name="手机号绑定";
        int plist_num=3;
        InfoItem info_phone=new InfoItem(plist_num,phone_icon,phone_name);
        infoList.add(info_phone);
        /**手机号绑定栏**/
    }
}
class InfoItem{
    private  int listNum;
    private Bitmap infoImg;
    private String infoTxt;
    public InfoItem(int listNum,Bitmap infoImg,String infoTxt){
        this.listNum=listNum;
        this.infoImg=infoImg;
        this.infoTxt=infoTxt;
    }

    public int getListNum() { return listNum; }

    public String getInfoTxt() {
        return infoTxt;
    }

    public Bitmap getInfoImg() {
        return infoImg;
    }
}
class InfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<InfoItem> infoList;
    public InfoAdapter(List<InfoItem> infoList){this.infoList=infoList;}
    static class InfoViewHolder extends RecyclerView.ViewHolder{
        LinearLayout image_layout;
        ImageView info_img;
        TextView info_txt;
        Button go_btn;
        public InfoViewHolder(View v) {
            super(v);
            image_layout=(LinearLayout) v.findViewById(R.id.image_layout);
            info_img=(ImageView)v.findViewById(R.id.info_img);
            info_txt=(TextView)v.findViewById(R.id.info_txt);
            go_btn=(Button)v.findViewById(R.id.go_btn);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.info_item,viewGroup,false);
        //填充并获取循环列表视图
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
    InfoItem infoItem=infoList.get(position);
    InfoViewHolder infoViewHolder=(InfoViewHolder)vh;
    int list_number=infoItem.getListNum();
    switch (list_number){
        case 0: infoViewHolder.info_img.setImageBitmap(infoItem.getInfoImg());
                infoViewHolder.info_txt.setText(infoItem.getInfoTxt());
                break;
        case 1:infoViewHolder.image_layout.setVisibility(View.GONE);
                infoViewHolder.info_txt.setText(infoItem.getInfoTxt());
                break;
        case 2:infoViewHolder.image_layout.setVisibility(View.GONE);
                infoViewHolder.info_txt.setText(infoItem.getInfoTxt());
                break;
        case 3:infoViewHolder.image_layout.setVisibility(View.GONE);
                infoViewHolder.info_txt.setText(infoItem.getInfoTxt());
                break;
        case 4:infoViewHolder.image_layout.setVisibility(View.GONE);
                infoViewHolder.info_txt.setText(infoItem.getInfoTxt());
                break;

    }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

}
class mDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable lineDivder;
    public mDividerItemDecoration(Context context){
        lineDivder=context.getResources().getDrawable(R.drawable.line_divder);
    }
    @Override
   public void onDrawOver(Canvas c,RecyclerView parent,RecyclerView.State state){
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

