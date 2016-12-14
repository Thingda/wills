package com.wills.help.login.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wills.help.R;
import com.wills.help.base.BaseActivity;
import com.wills.help.login.model.User;
import com.wills.help.login.presenter.LoginPresenterImpl;
import com.wills.help.login.view.LoginView;
import com.wills.help.utils.IntentUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * com.wills.help.login.ui
 * Created by lizhaoyong
 * 2016/11/16.
 */

public class LoginActivity extends BaseActivity implements LoginView ,View.OnClickListener{
    ImageView imageView;
    EditText et_username,et_password;
    TextView tv_unlogin,tv_register;
    Button btn_login;
    private LoginPresenterImpl loginInfoPresenter;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setNoActionBarView(R.layout.activity_login);
        imageView = (ImageView) findViewById(R.id.iv_avatar);
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_unlogin = (TextView) findViewById(R.id.tv_unlogin);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        loginInfoPresenter = new LoginPresenterImpl(this);
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_unlogin.setOnClickListener(this);
        et_username.setText("15652956043");
        et_password.setText("qqq");
    }

    @Override
    public void setLogin(User login) {
        tv_register.setText(login.toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                String user = et_username.getText().toString();
                String pwd = et_password.getText().toString();
                Map<String,String> map = new HashMap<>();
                map.put("username",user);
                map.put("password", pwd);
                loginInfoPresenter.login(map);
                break;
            case R.id.tv_register:
                IntentUtils.startActivity(context,RegisterActivity.class);
                break;
            case R.id.tv_unlogin:
                break;
        }
    }
}
