package com.kariyernet.marketim.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kariyernet.marketim.R;
import com.kariyernet.marketim.adapter.OrdersAdapter;
import com.kariyernet.marketim.model.OrdersBase;
import com.kariyernet.marketim.ui.login.Login;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Main extends AppCompatActivity implements MainContract.View {


    RecyclerView rvOrders;
    Button btnOrders;
    Button btnLogout;
    LinearLayout linNoData;
    MainPresenter mMainPresenter;
    Context mContext;
    OrdersAdapter ordersAdapter;
    SweetAlertDialog swLoadingDialog;
    SweetAlertDialog swLogoutDialog;
    SweetAlertDialog swError;

    Toolbar toolbar;
    TextView toolbarTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_main);
        mContext=this;


        // MVP modeline göre MainPresenter'in tanımlanması
        mMainPresenter = new MainPresenter();
        mMainPresenter.setView(this);
        mMainPresenter.created();

    }



    @Override
    public void bindView() {  // View 'ların bind edilmesi

        rvOrders = findViewById(R.id.rvOrders);
        btnOrders = findViewById(R.id.btnOrders);
        btnLogout = findViewById(R.id.btnLogout);
        toolbar = findViewById(R.id.toolbar);
        linNoData = findViewById(R.id.linNoData);
        toolbarTitle= toolbar.findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(mContext.getResources().getString(R.string.app_name));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        swLoadingDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        swLoadingDialog.getProgressHelper().setBarColor(mContext.getResources().getColor(R.color.colorPleaseWait));
        swLoadingDialog.setTitleText(mContext.getString(R.string.trl_please_wait));
        swLoadingDialog.setCancelable(false);
        mMainPresenter.requestDataFromServer();
    }

    @Override
    public void initOnClicks() { // View Click işlemleri (Orders listesinin getirilmesi ve Logout işlemi)

        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainPresenter.requestDataFromServer();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                swLogoutDialog= new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
                swLogoutDialog.setTitleText(mContext.getResources().getString(R.string.trl_logout_message));
                swLogoutDialog.setCancelText(mContext.getResources().getString(R.string.trl_no));
                swLogoutDialog.setConfirmText(mContext.getResources().getString(R.string.trl_yes));
                swLogoutDialog.showCancelButton(true);
                swLogoutDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {

                        sDialog.cancel();

                    }
                });
                swLogoutDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        mMainPresenter.checkLogout();
                        Intent mIntent = new Intent(Main.this, Login.class);
                        Main.this.startActivity(mIntent);
                        Main.this.finish();
                    }
                });
                swLogoutDialog.show();

            }
        });
    }

    @Override
    public void setDataToRecyclerView(List<OrdersBase> orderList) {  // Servisten gelen datanın adapter'e aktarılması, hiç veri olmaması durumunda ilgili uyarının gösterilmesi

        if(orderList.size()>0)
        {

            ordersAdapter = new OrdersAdapter(orderList);
            rvOrders.setAdapter(ordersAdapter);
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvOrders.setLayoutManager(layoutManager);
            ordersAdapter.notifyDataSetChanged();
            linNoData.setVisibility(View.GONE);
        }
        else
        {
            linNoData.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void showLoadingDialog() { // Servisten data getiriliken Loading dialogu gösterilmesi
        swLoadingDialog.show();
    }

    @Override
    public void hideLoadingDialog() { // Servisten data getiriliken Loading dialogu gizlenmesi
        swLoadingDialog.dismiss();
    }

    @Override
    public void showWrongData() {  // Servis ilgili gelen data 'da problem olması durumunda uyarı gösterilmesi
        swError = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        swError.getProgressHelper().setBarColor(mContext.getResources().getColor(R.color.colorPleaseWait));
        swError.setTitleText(mContext.getString(R.string.trl_something_went_wrong));
        swError.setCancelable(false);
        swError.show();
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void showNoInternetConnection() {  // Kullanıcının internen bağlantısı olmaması durumunda uyarı gösterilmesi
        swError = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        swError.getProgressHelper().setBarColor(mContext.getResources().getColor(R.color.colorPleaseWait));
        swError.setTitleText(mContext.getString(R.string.trl_no_internet_connection));
        swError.setCancelable(false);
        swError.show();
    }


}
