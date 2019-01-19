package com.design.lxp.taxiguard;

import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class TabHostActivity extends TabActivity implements View.OnClickListener {
    private LinearLayout tab_first,tab_second,tab_third,tab_fourth;
    private TabHost tab_host;
    private static final String TAG="TabHostActivity";
    private String FIRST_TAG="first";
    private String SECOND_TAG="second";
    private String THIRD_TAG="third";
    private String FOURTH_TAG="fourth";
    private Bundle mBundle=new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_host);
        if(getUser()!=null){
            mBundle.putString("tab_user",getUser());
        }else if(getEUser()!=null){
            mBundle.putString("evulateTotab_user",getEUser());
        }
        tab_first=(LinearLayout)findViewById(R.id.tab_first);
        tab_second=(LinearLayout)findViewById(R.id.tab_second);
        tab_third=(LinearLayout)findViewById(R.id.tab_third);
        tab_fourth=(LinearLayout)findViewById(R.id.tab_fourth);
        tab_first.setOnClickListener(this);
        tab_second.setOnClickListener(this);
        tab_third.setOnClickListener(this);
        tab_fourth.setOnClickListener(this);

        tab_host=getTabHost();
        tab_host.addTab(getNewTab(FIRST_TAG,R.string.menu_first,R.drawable.tab_bg_selector,FriendActivity.class));
        tab_host.addTab(getNewTab(SECOND_TAG,R.string.menu_second,R.drawable.tab_bg_selector,ChatActivity.class));
        tab_host.addTab(getNewTab(THIRD_TAG,R.string.menu_third,R.drawable.tab_bg_selector,ShareActivity.class));
        tab_host.addTab(getNewTab(FOURTH_TAG,R.string.menu_fourth,R.drawable.tab_bg_selector,InfoActivity.class));
        tab_host.setCurrentTabByTag(FIRST_TAG);
        changeContainerView(tab_first);
    }
    private TabHost.TabSpec getNewTab(String spec,int label,int icon,Class<?>cls){
        Intent intent=new Intent(this,cls).putExtras(mBundle);
        return tab_host.newTabSpec(spec).setContent(intent)
                .setIndicator(getString(label),getResources().getDrawable(icon));
    }
    private void changeContainerView(View v){
        tab_first.setSelected(false);
        tab_second.setSelected(false);
        tab_third.setSelected(false);
        tab_fourth.setSelected(false);
        v.setSelected(true);
        if(v==tab_first){
            tab_host.setCurrentTabByTag(FIRST_TAG);
        }else if(v==tab_second){
            tab_host.setCurrentTabByTag(SECOND_TAG);
        }
        else if(v==tab_third){
            tab_host.setCurrentTabByTag(THIRD_TAG);
        }else if(v==tab_fourth){
            tab_host.setCurrentTabByTag(FOURTH_TAG);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tab_first||v.getId()==R.id.tab_second||v.getId()==R.id.tab_third||v.getId()==R.id.tab_fourth){
            changeContainerView(v);
        }
    }
    public String getUser(){
        Intent tab_intent=getIntent();
        return tab_intent.getStringExtra("login_user");
    }
    public String getEUser(){
        Intent tab_intent=getIntent();
        return tab_intent.getStringExtra("evulateTotab_user");
    }
}
