package com.wills.help.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EasyUtils;
import com.wills.help.R;
import com.wills.help.assist.ui.AssistFragment;
import com.wills.help.db.bean.Contact;
import com.wills.help.db.manager.ContactHelper;
import com.wills.help.home.ui.HomeFragment;
import com.wills.help.login.ui.LoginActivity;
import com.wills.help.message.ContactsView;
import com.wills.help.message.controller.EaseUI;
import com.wills.help.message.presenter.ContactsPresenterImpl;
import com.wills.help.person.ui.PersonFragment;
import com.wills.help.release.ui.MainReleaseFragment;
import com.wills.help.utils.AppConfig;
import com.wills.help.utils.AppManager;
import com.wills.help.utils.IntentUtils;
import com.wills.help.utils.ScreenUtils;
import com.wills.help.utils.StringUtils;
import com.wills.help.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener , ContactsView{
    private FrameLayout frameLayout;
    private BottomNavigationBar bottomNavigationBar;
    private static ArrayList<Fragment> fragments;
    private long mExitTime = 0;
    private static String[] tags ;
    private Fragment currentView;
    private HomeFragment homeFragment;
    private MainReleaseFragment mainReleaseFragment;
    private AssistFragment assistFragment;
    private PersonFragment personFragment;
    private final int LOGIN = 11;
    private int currentPosition = 0;
    private int prevPosition = 0;
    private RelativeLayout rl_root;
    private MainReleaseFragment releaseFragment;//用于不同页面跳转展示不同数据时使用。
    private boolean isRelease = false;//是切换发布需求的
//    private BottomNavigationItem msgItem;
    private ContactsPresenterImpl contactsPresenter;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setNoActionBarView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.fl_content);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        tags = new String[]{getString(R.string.tab_home), getString(R.string.tab_release) , getString(R.string.tab_assist), getString(R.string.tab_person)};
        initBottomNavigationBar();
        stateCheck(savedInstanceState);
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
        contactsPresenter = new ContactsPresenterImpl(this);
        if (App.getApp().getIsLogin()){
            Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
            if (conversations.size()>0){
                if (!(conversations.size() == 1&&conversations.containsKey("admin"))){
                    contactsPresenter.getContacts(getMap(conversations));
                }
            }
        }
    }

    private void initBottomNavigationBar(){
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setTabSelectedListener(this);
//        msgItem = new BottomNavigationItem(R.drawable.tab_msg, R.string.tab_msg).setActiveColorResource(R.color.colorPrimaryDark);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.tab_home , R.string.tab_home).setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.tab_release, R.string.tab_release).setActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.tab_assist, R.string.tab_assist).setActiveColorResource(R.color.colorPrimaryDark))
//                .addItem(msgItem)
                .addItem(new BottomNavigationItem(R.drawable.tab_person, R.string.tab_person).setActiveColorResource(R.color.colorPrimaryDark))
                .setFirstSelectedPosition(0)
                .initialise();
        if (ScreenUtils.getTabHeight() == 0){
            rl_root = (RelativeLayout) findViewById(R.id.rl_root);
            rl_root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int barHeight = bottomNavigationBar.getHeight();
                    if (barHeight != 0){
                        rl_root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        ScreenUtils.setTabHeight(barHeight);
                    }
                }
            });
        }
        fragments = getFragments();
//        AddBadge(5);
    }

    private void setDefaultFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fl_content,fragments.get(0),tags[0]);
        transaction.commit();
        currentView = fragments.get(0);
    }

    private void stateCheck(Bundle savedInstanceState){
        if (savedInstanceState == null){
            setDefaultFragment();
        }else{
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(tags[0]);
            mainReleaseFragment = (MainReleaseFragment) getSupportFragmentManager().findFragmentByTag(tags[1]);
            assistFragment = (AssistFragment) getSupportFragmentManager().findFragmentByTag(tags[2]);
            personFragment = (PersonFragment) getSupportFragmentManager().findFragmentByTag(tags[3]);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (mainReleaseFragment!=null){
                transaction.hide(mainReleaseFragment);
            }
            if (assistFragment!=null){
                transaction.hide(assistFragment);
            }
            if (personFragment!=null){
                transaction.hide(personFragment);
            }
            if (homeFragment!=null){
                transaction.show(homeFragment);
            }
            transaction.commit();
            fragments = getFragments();
            currentView = fragments.get(0);
        }
    }

    private ArrayList<Fragment> getFragments(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        homeFragment = HomeFragment.newInstance();
        fragments.add(homeFragment);
        mainReleaseFragment = MainReleaseFragment.newInstance(0,0);
        fragments.add(mainReleaseFragment);
        assistFragment = AssistFragment.newInstance();
        fragments.add(assistFragment);
//        fragments.add(MessageFragment.newInstance());
        personFragment = PersonFragment.newInstance();
        fragments.add(personFragment);
        return fragments;
    }

    private void switchView(Fragment to , int position){
        if (isRelease){
            return;
        }
        if (currentView != to){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            if (!to.isAdded()){
                transaction.hide(currentView).add(R.id.fl_content,to,tags[position]).commitAllowingStateLoss();
            }else {
                transaction.hide(currentView).show(to).commitAllowingStateLoss();
            }
        }
        currentView = to;
    }

    private void changeReleaseFragment(int tabIndex,int stateId){
        releaseFragment = MainReleaseFragment.newInstance(tabIndex,stateId);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(currentView).add(R.id.fl_content,releaseFragment).commitAllowingStateLoss();
        currentView = releaseFragment;
        isRelease = false;
    }

//    private void AddBadge(int number){
//        BadgeItem numberBadgeItem = new BadgeItem()
//                .setBorderWidth(5)
//                .setBackgroundColorResource(R.color.red)
//                .setText(number+"")
//                .setHideOnSelect(true);
//        msgItem.setBadgeItem(numberBadgeItem);
//        bottomNavigationBar.initialise();
//    }

    @Override
    public void onTabSelected(int position) {
        if (fragments !=null){
            currentPosition = position;
            if (position<fragments.size()){
                if (position!=0){
                    if (!App.getApp().getIsLogin()){
                        IntentUtils.startActivityForResult(this, LoginActivity.class,LOGIN);
                        return;
                    }
                }
                Fragment fragment = fragments.get(position);
                switchView(fragment,position);
                prevPosition = position;
            }
        }
    }

    @Override
    public void onTabUnselected(int position) {
//        if (fragments != null) {
//            if (position < fragments.size()) {
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                Fragment fragment = fragments.get(position);
//                ft.remove(fragment);
//                ft.commitAllowingStateLoss();
//            }
//        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis() - mExitTime) > 2000){
                ToastUtils.toast(R.string.back_exit);
                mExitTime = System.currentTimeMillis();
            }else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==LOGIN){
            if (resultCode==RESULT_OK)
                bottomNavigationBar.selectTab(currentPosition);
            else if (resultCode==RESULT_CANCELED)
                bottomNavigationBar.selectTab(prevPosition);
        } else if (requestCode == 301&&resultCode==RESULT_OK){
            isRelease = true;
            bottomNavigationBar.selectTab(1);
            changeReleaseFragment(1,0);
        }else if (requestCode == 12&&resultCode==RESULT_OK){
            bottomNavigationBar.selectTab(0);
        }else if(requestCode == 401 && resultCode == RESULT_OK){
            mainReleaseFragment.onActivityResult(requestCode,resultCode,data);
        }else if (requestCode == AppConfig.AVATAR && resultCode == RESULT_OK){
            personFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    public BottomNavigationBar getBottomNavigationBar(){
        if (bottomNavigationBar==null){
            bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        }
        return bottomNavigationBar;
    }

    public void jumpReleaseFragment(int position,int stateId){
        isRelease = true;
        bottomNavigationBar.selectTab(position);
        changeReleaseFragment(0,stateId);
    }

    EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            if (!EasyUtils.isAppRunningForeground(context)){
                EaseUI.getInstance().getNotifier().onNewMesg(list);
            }else {
                String currentActivity = AppManager.getAppManager().currentActivity().getClass().getSimpleName();
                if (!currentActivity.equals("MessageActivity")&&!currentActivity.equals("ChatActivity")){
                    EaseUI.getInstance().getNotifier().onNewMesg(list);
                }
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            for (EMMessage message : list){
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                String action = cmdMsgBody.action();
                if (action.equals("userInfo")){
                    Map<String ,String> map = new HashMap<>();
                    map.put("usernames",message.getUserName());
                    contactsPresenter.getContacts(map);
                }
            }
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }

    @Override
    public void setContacts(List<Contact> contactList) {
        ContactHelper.getInstance().insertData(contactList).subscribe();
    }

    private Map<String,String> getMap(Map<String, EMConversation> conversations){
        String userNames = "";
        Map<String,String> map = new HashMap<>();
        for (Map.Entry<String,EMConversation> entry:conversations.entrySet()){
            if (!entry.getKey().equals("admin")){
                userNames+=entry.getKey()+",";
            }
        }
        if (!StringUtils.isNullOrEmpty(userNames)){
            map.put("usernames",userNames.substring(0,userNames.length()-1));
        }
        return map;
    }
}
