package com.example.audioplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.audioplayer.mineFragment.MineFragment;
import com.example.audioplayer.playerActivity.PlayerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.os.IBinder;
import android.view.MenuItem;
import android.view.WindowManager;
import com.example.audioplayer.searchFragment.SearchFragment;

import java.io.File;

import Config.*;
import MediaService.MediaService;
import MediaService.exposedServices;
import Bean.*;
public class MainActivity extends AppCompatActivity {


    private SearchFragment searchFragment = null;
    private MineFragment mineFragment = null;
    public exposedServices iService;
    final private myConnection conn = new myConnection();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:{
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if(searchFragment == null)
                        searchFragment = new SearchFragment();
                    fragmentTransaction.replace(R.id.fragment_display,searchFragment).commit();
                    return true;
                }
                case R.id.navigation_mine:{
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if(mineFragment == null)
                        mineFragment = new MineFragment();
                    fragmentTransaction.replace(R.id.fragment_display, mineFragment).commit();
                    return true;
                }
                case R.id.navigation_setting:{
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File dir = new File(GlobalData.dirPath);

        if(!dir.exists()) {
            dir.mkdirs();
        }

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    //return;
                }
            }
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(searchFragment == null)
            searchFragment = new SearchFragment();


        fragmentTransaction.replace(R.id.fragment_display,searchFragment).commit();

        bindService(new Intent(this, MediaService.class),conn,BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        finish();
        System.exit(0);
    }


    public class myConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iService = (exposedServices) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iService = null;
        }
    }

}
