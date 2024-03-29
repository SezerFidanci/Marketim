package com.kariyernet.marketim.ui.login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import com.kariyernet.marketim.App;
import com.kariyernet.marketim.R;
import com.kariyernet.marketim.ui.main.Main;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity implements LoginContract.View{

    Button btnLogin;
    EditText edtUsername;
    EditText edtPassword;
    Switch swKeepLogin;
    boolean isKeepLogin=false;
    LoginPresenter mLoginPresenter;
    SweetAlertDialog swLoginError;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_login);
        mContext=this;

        // MVP modeline göre LoginPresenter'in tanımlanması
        mLoginPresenter = new LoginPresenter();
        mLoginPresenter.setView(this);
        mLoginPresenter.created();

    }

    @Override
    public void bindView() { // View'ların bind edilmesi
        btnLogin = findViewById(R.id.btnLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        swKeepLogin = findViewById(R.id.swKeepLogin);

    }

    @Override
    public void initOnClicks() { // View Click işlemleri (Login ve Oturumu açık tut)

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = edtUsername.getText().toString().trim();
                String uPass = edtPassword.getText().toString().trim();
                boolean loginCheck = mLoginPresenter.checkLogin(uName,uPass);
                if(loginCheck)
                {
                    App.prefsSet.putBoolean("isKeepLogin",isKeepLogin);
                    App.prefsSet.commit();
                    Intent mIntent = new Intent(Login.this, Main.class);
                    Login.this.startActivity(mIntent);
                    Login.this.finish();
                }
                else
                {
                    swLoginError = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
                    swLoginError.getProgressHelper().setBarColor(mContext.getResources().getColor(R.color.colorPleaseWait));
                    swLoginError.setTitleText(mContext.getString(R.string.trl_login_error));
                    swLoginError.setCancelable(false);
                    swLoginError.show();
                }
            }
        });

        swKeepLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   isKeepLogin=true;
                } else {
                    isKeepLogin=false;
                }
            }
        });

    }


}
