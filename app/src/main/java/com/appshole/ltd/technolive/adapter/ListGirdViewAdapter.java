package com.appshole.ltd.technolive.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appshole.ltd.technolive.R;
import com.appshole.ltd.technolive.model.Board;
import com.appshole.ltd.technolive.model.BoardCardList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ListGirdViewAdapter extends BaseAdapter {
    /**
     * init variable
     */
    private ArrayList<BoardCardList> boardCardListArrayList;
    public Context context;
    public LayoutInflater inflater;
    private onSelectedListGirdViewAdapterListener m_onSelectedListGirdViewAdapterListener;


    public ListGirdViewAdapter(Context context, ArrayList<BoardCardList> boardCardListArrayList, onSelectedListGirdViewAdapterListener m_onSelectedListGirdViewAdapterListener) {
        super();
        this.context = context;
        this.boardCardListArrayList = boardCardListArrayList;
        this.m_onSelectedListGirdViewAdapterListener = m_onSelectedListGirdViewAdapterListener;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return boardCardListArrayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        /**
         * load list view
         */

        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();


            convertView = inflater.inflate(R.layout.listview_gird_view_adapter, null);


            holder.imgAttachImage = (ImageView) convertView.findViewById(R.id.imgAttachImage);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BoardCardList requisitionInfo = (BoardCardList)v.getTag();
                    m_onSelectedListGirdViewAdapterListener.onClick(2, requisitionInfo);
                }
            });



            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {


            final BoardCardList requisitionInfo = boardCardListArrayList.get(position);


            Glide.with(context)
                    .load(requisitionInfo.getUrl())
                    .placeholder(R.drawable.card_cover_placeholder)
                    .crossFade().override(200, 200)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgAttachImage);


            holder.btnDelete.setTag(requisitionInfo);

            convertView.setTag(holder);

        } catch (Exception e) {
            Log.v("dd", e.toString());
        }

        return convertView;
    }


    public static class ViewHolder {

        private ImageView imgAttachImage;
        private Button btnDelete;


    }

    public void setData(ArrayList<BoardCardList> boardCardListArrayList) {
        // TODO Auto-generated method stub
        this.boardCardListArrayList = boardCardListArrayList;
    }

    public interface onSelectedListGirdViewAdapterListener {
        void onClick(int type, BoardCardList myboard);
    }


}