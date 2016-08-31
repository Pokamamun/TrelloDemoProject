package com.appshole.ltd.technolive.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.appshole.ltd.technolive.R;
import com.appshole.ltd.technolive.model.Board;

import java.util.ArrayList;

public class ListViewMyBoardAdapter extends BaseAdapter {
    /**
     * init variable
     */
    private ArrayList<Board> myboardArray;
    public Context context;
    public LayoutInflater inflater;
    private onSelectedBoardListener m_onSelectedBoardListener;


    public ListViewMyBoardAdapter(Context context, ArrayList<Board> myboardArray, onSelectedBoardListener m_onSelectedBoardListener) {
        super();
        this.context = context;
        this.myboardArray = myboardArray;
        this.m_onSelectedBoardListener = m_onSelectedBoardListener;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return myboardArray.size();
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


            convertView = inflater.inflate(R.layout.listview_my_board, null);


            holder.txtBoardName = (TextView) convertView.findViewById(R.id.txtBoardName);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    try {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        Board requisitionInfo = (Board) viewHolder.txtBoardName.getTag();
                        m_onSelectedBoardListener.onClick(1, requisitionInfo);
                    } catch (Exception e) {
                        e.getMessage();
                    }


                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ViewHolder viewHolder = (ViewHolder) v.getTag();
                    Board requisitionInfo = (Board) viewHolder.txtBoardName.getTag();
                    m_onSelectedBoardListener.onClick(2, requisitionInfo);
                    return true;
                }
            });


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {


            final Board requisitionInfo = myboardArray.get(position);


            holder.txtBoardName.setText(requisitionInfo.getBoardName() + "");


            holder.txtBoardName.setTag(requisitionInfo);

            convertView.setTag(holder);

        } catch (Exception e) {
            Log.v("dd", e.toString());
        }

        return convertView;
    }


    public static class ViewHolder {

        private TextView txtBoardName;


    }

    public void setData(ArrayList<Board> myboardArray) {
        // TODO Auto-generated method stub
        this.myboardArray = myboardArray;
    }

    public interface onSelectedBoardListener {
        void onClick(int type, Board myboard);
    }


}