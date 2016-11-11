package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Intent;

import com.hyphenate.easeui.domain.User;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.FriendProfileActivity;
import cn.ucai.superwechat.ui.GroupsActivity;
import cn.ucai.superwechat.ui.GuideActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.NewFriendsMsgActivity;
import cn.ucai.superwechat.ui.NewGroupActivity;
import cn.ucai.superwechat.ui.PublicGroupsActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SendAddRequestActivity;
import cn.ucai.superwechat.ui.SettingsActivity;
import cn.ucai.superwechat.ui.UserProfileActivity;


/**
 * 实现跳转
 */
public class MFGT {
    public static void finish(Activity activity){
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }

    public static void startActivity(Activity context,Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        startActivity(context,intent);
    }

    public static void startActivity(Activity context,Intent intent){
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }


    public static void startActivityForResult(Activity context,Intent intent,int requestCode){
        context.startActivityForResult(intent,requestCode);
        context.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

    public static void gotoLogin(Activity context){
        startActivity(context, LoginActivity.class);
    }

    public static void gotoRegister(Activity context){
        startActivity(context, RegisterActivity.class);
    }
    public static void gotoSetting(Activity context){
        startActivity(context, SettingsActivity.class);
    }
    public static void gotoUserProfile(Activity context){
        startActivity(context, UserProfileActivity.class);
    }
    public static void gotoAddFriend(Activity context){
        startActivity(context, AddContactActivity.class);
    }


    public static void gotoFriendProfile(Activity context,String username) {
        Intent intent = new Intent();
        intent.setClass(context,FriendProfileActivity.class);
        intent.putExtra(I.User.USER_NAME,username);
        startActivity(context, intent);
    }
    public static void gotoSendAddRequest(Activity context,String username) {
        L.e("我也很想跳啊！！！！");
        Intent intent = new Intent();
        intent.setClass(context,SendAddRequestActivity.class);
        intent.putExtra(I.User.USER_NAME,username);
        startActivity(context, intent);
        L.e("他就是不跳，QAQ！！！");
    }
    public static void gotoNewFriendsMsg(Activity context) {
        startActivity(context, NewFriendsMsgActivity.class);
    }

    public static void gotoChat(Activity context,String username) {
        Intent intent = new Intent();
        intent.setClass(context,ChatActivity.class);
        intent.putExtra("userId",username);
        startActivity(context, intent);
    }
    public static void gotoGroups(Activity context) {
        startActivity(context, GroupsActivity.class);
    }
    public static void gotoNewGroup(Activity context) {
        startActivity(context, NewGroupActivity.class);
    }
    public static void gotoPublicGroups(Activity context) {
        startActivity(context, PublicGroupsActivity.class);
    }
}
