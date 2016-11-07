package cn.ucai.superwechat.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        ButterKnife.bind(this);

        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (user==null){
            MFGT.finish(this);
        }
        initView();
    }

    private void initView() {
        imgBack.setVisibility(View.VISIBLE);
        txtMtitle.setVisibility(View.VISIBLE);
        txtMtitle.setText(getString(R.string.userinfo_txt_profile));

        setUserUnfo();
    }

    private void setUserUnfo() {
        EaseUserUtils.setAppUserAvatar(this,user.getMUserName(),ivfriendprofileavatar);
        EaseUserUtils.setAppUserNick(user.getMUserName(),tvprofilenickname);
        EaseUserUtils.setAppUserNameWithNo(user.getMUserName(),tvprofileusername);
    }

    @OnClick(R.id.img_back)
    public void onClick(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
