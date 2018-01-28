package com.deep.app.views;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.deep.app.R;
import com.deep.app.utils.Tools;
import net.lightbody.bmp.core.har.HarEntry;

/**
 * Created by wangfei on 2017/10/17.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyHolder>{

    private List<HarEntry> mList;

    public HomeAdapter(List<HarEntry> list) {
        super();
        this.mList = list;
    }
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

        void OnItemLongClick(View view, int position);
    }
    public HomeAdapter.OnItemClickListener mOnItemClickListener;

    public void SetOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.button_item,
            parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {



        holder.urlTv.setText(mList.get(position).getRequest().getUrl());
        holder.repTv.setText("response status:"+mList.get(position).getResponse().getStatusText());
        holder.timeTv.setText("发出时间   "+ Tools.formatData(mList.get(position).getStartedDateTime()));
        holder.wasteTv.setText("耗时:"+mList.get(position).getTime());
        holder.methodTv.setText("请求方式:"+mList.get(position).getRequest().getMethod());
        if (mOnItemClickListener != null) {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.OnItemClick(holder.itemView, position);
                }

            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.OnItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        public TextView urlTv;
        public TextView timeTv;
        public TextView repTv;
        public TextView methodTv;
        public TextView wasteTv;
        public MyHolder(View View) {
            super(View);
            urlTv = (TextView) View.findViewById(R.id.url);
            timeTv = (TextView) View.findViewById(R.id.time);
            repTv = (TextView) View.findViewById(R.id.response);
            methodTv = (TextView) View.findViewById(R.id.method);
            wasteTv = (TextView) View.findViewById(R.id.waste);
        }
    }
}
