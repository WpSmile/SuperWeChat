package cn.ucai.superwechat.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.MFGT;


public class FriendProfileActivity extends BaseActivity {

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

    User user;
    @Bind(R.id.bt_friend_profile_send)
    Button btFriendProfileSend;
    @Bind(R.id.bt_friend_profile_chat)
    Button btFriendProfileChat;
    @Bind(R.id.bt_friend_profile_add)
    Button btFriendProfileAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);

        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (user == null) {
            MFGT.finish(this);
        }
        initView();
    }

    private void initView() {
        imgBack.setVisibility(View.VISIBLE);
        txtMtitle.setVisibility(View.VISIBLE);
        txtMtitle.setText(getString(R.string.userinfo_txt_profile));

        setUserUnfo();
        isFriend();
    }

    private void setUserUnfo() {
        EaseUserUtils.setAppUserAvatar(this, user.getMUserName(), ivfriendprofileavatar);
        EaseUserUtils.setAppUserNick(user.getMUserNick(), tvprofilenickname);
        EaseUserUtils.setAppUserNameWithNo(user.getMUserName(), tvprofileusername);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void isFriend() {
        if (SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())){
            btFriendProfileSend.setVisibility(View.VISIBLE);
            btFriendProfileChat.setVisibility(View.VISIBLE);
        }else {
            btFriendProfileAdd.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.bt_friend_profile_send, R.id.bt_friend_profile_chat, R.id.bt_friend_profile_add,R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_friend_profile_send:
                break;
            case R.id.bt_friend_profile_chat:
                break;
            case R.id.bt_friend_profile_add:
                MFGT.gotoSendAddRequest(this,user.getMUserName());
                break;
            case R.id.img_back:
                MFGT.finish(this);
                break;
        }
    }
}
