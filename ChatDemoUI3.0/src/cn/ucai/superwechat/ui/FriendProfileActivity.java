package cn.ucai.superwechat.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;


public class FriendProfileActivity extends BaseActivity {

    private static final String TAG = FriendProfileActivity.class.getCanonicalName();

    @Bind(R.id.img_back)
    ImageView imgBack;
    @Bind(R.id.txt_mtitle)
    TextView txtMtitle;
    @Bind(R.id.iv_friend_profile_avatar)
    ImageView ivfriendprofileavatar;
    @Bind(R.id.tv_profile_nickname)
    TextView tvprofilenickname;
    @Bind(R.id.tv_profile_username)
    TextView tvprofileusername;

    String username;
    User user;
    @Bind(R.id.bt_friend_profile_send)
    Button btFriendProfileSend;
    @Bind(R.id.bt_friend_profile_chat)
    Button btFriendProfileChat;
    @Bind(R.id.bt_friend_profile_add)
    Button btFriendProfileAdd;
    boolean isFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);
        //user1 = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        //username = user1.getMUserName();
        username = getIntent().getStringExtra(I.User.USER_NAME);

        L.e(TAG, "username====" + username);
        if (username == null) {
            MFGT.finish(this);
            return;
        }
        user = SuperWeChatHelper.getInstance().getAppContactList().get(username);
        L.e(TAG,"user===="+user);
        initView();
        if (user == null) {
            isFriend = false;
        } else {
            L.e(TAG,"myUsername====="+username);
            setUserUnfo();
            isFriend = true;
        }
        isFriend(isFriend);
        syncUserInfo();
    }

    private void initView() {
        imgBack.setVisibility(View.VISIBLE);
        txtMtitle.setVisibility(View.VISIBLE);
        txtMtitle.setText(getString(R.string.userinfo_txt_profile));

    }

    private void setUserUnfo() {
        EaseUserUtils.setAppUserAvatar(this, user.getMUserName(), ivfriendprofileavatar);
        EaseUserUtils.setAppUserNick(user.getMUserNick(), tvprofilenickname);
        EaseUserUtils.setAppUserNameWithNo(user.getMUserName(), tvprofileusername);
    }


    private void syncUserInfo() {
        NetDao.syncUserInfo(this, username, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.isRetMsg()) {
                        User u = (User) result.getRetData();
                        if (u != null) {

                            if (isFriend) {
                                SuperWeChatHelper.getInstance().saveAppContact(u);
                            }
                            user = u;
                            setUserUnfo();
                        } else {
                            syncFail();
                        }
                    } else {
                        syncFail();
                    }
                } else {
                    syncFail();
                }
            }

            @Override
            public void onError(String error) {
                syncFail();
            }
        });
    }

    private void syncFail() {
        if (isFriend){
            MFGT.finish(this);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void isFriend(boolean isFriend) {
        if (isFriend) {
            btFriendProfileSend.setVisibility(View.VISIBLE);
            btFriendProfileChat.setVisibility(View.VISIBLE);
        } else {
            btFriendProfileAdd.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.img_back, R.id.bt_friend_profile_send, R.id.bt_friend_profile_chat, R.id.bt_friend_profile_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                MFGT.finish(this);
                break;
            case R.id.bt_friend_profile_send:
                MFGT.gotoChat(this, user.getMUserName());
                break;
            case R.id.bt_friend_profile_chat:
                if (!EMClient.getInstance().isConnected())
                    Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", user.getMUserName())
                            .putExtra("isComingCall", false));
                    // videoCallBtn.setEnabled(false);
                }
                break;
            case R.id.bt_friend_profile_add:
                MFGT.gotoSendAddRequest(this, user.getMUserName());
                break;
        }
    }
   /* public void onshow(View view){
        MFGT.gotoSendAddRequest(this,user.getMUserName());
    }*/
}
