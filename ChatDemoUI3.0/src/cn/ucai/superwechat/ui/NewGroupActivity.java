/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.easeui.domain.Group;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.ta.utdid2.android.utils.SystemUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class NewGroupActivity extends BaseActivity {
    private static final String TAG = NewGroupActivity.class.getCanonicalName();
    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    private static final int REQUESTCODE_PICK_MEMBER = 3;

    @Bind(R.id.img_back)
    ImageView imgBack;
    @Bind(R.id.txt_mtitle)
    TextView txtMtitle;
    @Bind(R.id.btn_send)
    Button btnSave;
    @Bind(R.id.edit_group_name)
    EditText groupNameEditText;
    @Bind(R.id.edit_group_introduction)
    EditText introductionEditText;
    @Bind(R.id.cb_public)
    CheckBox publibCheckBox;
    @Bind(R.id.second_desc)
    TextView secondTextView;
    @Bind(R.id.cb_member_inviter)
    CheckBox memberCheckbox;
    @Bind(R.id.iv_groups_avatar)
    ImageView ivGroupsAvatar;


    private ProgressDialog progressDialog;
    File avatarFile = null;
    EMGroup emGroup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_group);
        ButterKnife.bind(this);
        initView();
        setListener();


    }

    private void setListener() {
        publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    secondTextView.setText(R.string.join_need_owner_approval);
                } else {
                    secondTextView.setText(R.string.Open_group_members_invited);
                }
            }
        });
    }

    private void initView() {
        imgBack.setVisibility(View.VISIBLE);
        txtMtitle.setVisibility(View.VISIBLE);
        txtMtitle.setText(R.string.build_new_group_chat);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setText(R.string.save);
        btnSave.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    /**
     * @param
     */
    public void save() {
        String name = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Group_name_cannot_be_empty).show();
        } else {
            // select from contact list
            startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), REQUESTCODE_PICK_MEMBER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            case REQUESTCODE_PICK_MEMBER:
                if (resultCode == RESULT_OK) {
                    createEMGroup(data);
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * save the picture data
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            ivGroupsAvatar.setImageDrawable(drawable);
            saveBitmapFile(picdata);
        }

    }

    private void createEMGroup(final Intent data) {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        final String st2 = getResources().getString(R.string.Failed_to_create_groups);

        //new group
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                try {
                    EMGroupOptions option = new EMGroupOptions();
                    option.maxUsers = 200;

                    String reason = NewGroupActivity.this.getString(R.string.invite_join_group);
                    reason = EMClient.getInstance().getCurrentUser() + reason + groupName;

                    if (publibCheckBox.isChecked()) {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
                    } else {
                        option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePrivateMemberCanInvite : EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                    emGroup = EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
                    createAppGroup(emGroup);
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void createAppGroup(EMGroup emGroup) {
        if (avatarFile == null) {
            NetDao.createGroup(this, emGroup, listener);
        } else {
            NetDao.createGroup(this, emGroup, avatarFile, listener);
        }
    }
    OkHttpUtils.OnCompleteListener<String> listener = new OkHttpUtils.OnCompleteListener<String>() {
        @Override
        public void onSuccess(String s) {
            if (s != null) {
                Result result = ResultUtils.getResultFromJson(s, Group.class);
                L.e(TAG,"result==="+result);
                if (result != null && result.isRetMsg()) {
                    if (emGroup!=null&&emGroup.getMembers()!=null&&emGroup.getMembers().size()>1){
                        addGroupMembers(emGroup);
                    }else {
                        createGroupSuccess();
                    }
                }
            }else {
                progressDialog.dismiss();
                CommonUtils.showShortToast(R.string.Failed_to_create_groups);
            }
        }

        @Override
        public void onError(String error) {
            progressDialog.dismiss();
            CommonUtils.showShortToast(R.string.Failed_to_create_groups);
        }
    };
    //add group members
    private void addGroupMembers(EMGroup emGroup) {
        NetDao.addGroupMembers(this, emGroup, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s!=null){
                    L.e(TAG,"addGroupMembers,s===="+s);
                    //Result result = ResultUtils.getResultFromJson(s, Group.class);
                }else {
                    CommonUtils.showShortToast(R.string.Failed_to_add_group_members);
                }
            }

            @Override
            public void onError(String error) {
                L.e(TAG,"addGroupMembers,error"+error);
                CommonUtils.showShortToast(R.string.Failed_to_add_group_members);
            }
        });

    }

    private void createGroupSuccess() {
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    @OnClick({R.id.img_back, R.id.btn_send, R.id.rl_groups_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                MFGT.finish(this);
                break;
            case R.id.btn_send:
                save();
                break;
            case R.id.rl_groups_avatar:
                uploadHeadPhoto();
                break;
        }
    }

    private void uploadHeadPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(NewGroupActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    public void saveBitmapFile(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            String imagePath = EaseImageUtils.getImagePath(System.currentTimeMillis() + I.AVATAR_SUFFIX_JPG);
            File file = new File(imagePath);//将要保存图片的路径
            L.e(TAG, "file path =" + file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            avatarFile = file;
        }
    }
}
