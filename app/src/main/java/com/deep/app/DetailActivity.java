package com.deep.app;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.deep.app.utils.Constants;
import com.deep.app.utils.Tools;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;

/**
 * Created by wangfei on 2018/1/11.
 */

public class DetailActivity extends AppCompatActivity {

    private HarEntry harEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Constants.harEntry!=null){
            harEntry = Constants.harEntry;
            if (getSupportActionBar()!=null){
                getSupportActionBar().setTitle(Tools.formatData(harEntry.getStartedDateTime()));
            }

        }
        getTextView(R.id.detail_url).setText("链接:"+ harEntry.getRequest().getUrl());
        getTextView(R.id.headers).setText(getListString(harEntry.getRequest().getHeaders()));
        getTextView(R.id.httpversion).setText("Http Version"+harEntry.getRequest().getHttpVersion());
        if (harEntry.getRequest().getPostData()!=null){
            getTextView(R.id.data).setText( harEntry.getRequest().getPostData().getText());
        }
        if (harEntry.getResponse()!=null){
            getTextView(R.id.status).setText( harEntry.getResponse().getStatusText());
            getTextView(R.id.status_text).setText("结果"+harEntry.getResponse().getStatus());
        }

        getTextView(R.id.serverip).setText("Server IP:"+harEntry.getServerIPAddress());


    }
    public TextView getTextView(int id){
        return (TextView)findViewById(id);
    }
    public String getListString(List<HarNameValuePair> list){
       StringBuilder sb = new StringBuilder();
        for (HarNameValuePair p :list){
            sb.append(p.getName()).append(" : ").append(p.getDecodeValue());
        }
      return sb.toString();
    }
}
