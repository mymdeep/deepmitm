package com.deep.app;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.security.KeyChain;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.deep.app.utils.Constants;
import com.deep.app.utils.SPUtils;
import com.deep.app.views.HomeAdapter;
import com.deep.app.views.HomeAdapter.OnItemClickListener;
import com.deepmitm.interfaces.AddCallBack;
import deep.com.deepproxy.log.Logger;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import util.IOUtil;

public class MainActivity extends AppCompatActivity {
    private Receiver receiver;
    HarLog harLog;
    private HomeAdapter mAdapter;
    List<HarEntry> harEntryList = new ArrayList<>();
    public RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("深度抓包");
        }

        initViews();
    }
    public void initViews(){

        mRecyclerView = (RecyclerView)findViewById(R.id.btn_list);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(Constants.SPACE));
        if(App.isInitProxy) {
            mAdapter = getmAdapter(getRealList());
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearList();
                Logger.single(0,"click");
            }
        });
        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(navigationItemListener);
        TextView tv = nav.getHeaderView(0).findViewById(R.id.port);
        tv.setText("端口号:"+App.proxyPort);
    }
    private ArrayList<HarEntry> getRealList(){
        ArrayList<HarEntry> realList = new ArrayList<>();
        harEntryList.clear();

        harEntryList.addAll(harLog.getEntries());
        for (HarEntry t : harEntryList){
            String url = t.getRequest().getUrl();
            Logger.single(0,"url="+url);
            Logger.single(0,t.getRequest().getUrl());
            if (url.contains(Constants.SUFFIX)){
                    realList.add(t);
            }
        }
        return realList;
    }
    private void clearList(){
        ((App)getApplication()).proxy.getHar().getLog().clearAllEntries();
        harEntryList.clear();
        if(App.isInitProxy) {
            mAdapter = getmAdapter(getRealList());
            mRecyclerView.setAdapter(mAdapter);

        }
    }
    private HomeAdapter getmAdapter(final ArrayList<HarEntry> list){
        HomeAdapter  adapter = new HomeAdapter(list);
        adapter.SetOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Intent intent  = new Intent(MainActivity.this,DetailActivity.class);
                Constants.harEntry = list.get(position);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        });
        return adapter;
    }
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            installCert();
            Logger.single(0, "Receiver installCert");
            harLog = ((App)getApplication()).proxy.getHar().getLog();
            harLog.setAddCallBack(new AddCallBack() {
                @Override
                public void call() {
                    Logger.single(0,"11111");
                }
            });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        receiver = new Receiver();
        registerReceiver(receiver, new IntentFilter("proxyfinished"));
       if (mRecyclerView!=null&&App.isInitProxy){
          mAdapter = getmAdapter(getRealList());
           mRecyclerView.setAdapter(mAdapter);

       }
    }
    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }
    public NavigationView.OnNavigationItemSelectedListener navigationItemListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (!App.isInitProxy) {
                Toast.makeText(MainActivity.this, "请等待程序初始化完成", Toast.LENGTH_LONG).show();
                return true;
            }
            if (id == R.id.nav_manage) {
                SPUtils.saveIsInstall(MainActivity.this,false);
                installCert();
            } else if (id == R.id.nav_rom) {
                Toast.makeText(MainActivity.this,"该功能暂未提供",Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_proxy) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

    };
    public void installCert() {
        final String CERTIFICATE_RESOURCE = Environment.getExternalStorageDirectory() + "/har/littleproxy-mitm.pem";
        Boolean isInstallCert = SPUtils.getIsInstall(this);

        if (!isInstallCert) {
        Toast.makeText(this, "必须安装证书才可实现HTTPS抓包", Toast.LENGTH_LONG).show();
        try {
            byte[] keychainBytes;

            FileInputStream is = null;
            try {
                is = new FileInputStream(CERTIFICATE_RESOURCE);
                keychainBytes = new byte[is.available()];
                is.read(keychainBytes);
            } finally {
                IOUtil.closeQuietly(is);
            }

            Intent intent = KeyChain.createInstallIntent();
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes);
            intent.putExtra(KeyChain.EXTRA_NAME, Constants.CERNAME);
            startActivityForResult(intent, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }
    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SPUtils.saveIsInstall(this, true);
                Toast.makeText(this, "安装成功", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "安装失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
