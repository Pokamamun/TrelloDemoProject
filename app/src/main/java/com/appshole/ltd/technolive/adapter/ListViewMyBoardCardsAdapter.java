package com.appshole.ltd.technolive.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appshole.ltd.technolive.R;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardCardListDao;
import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardDao;
import com.appshole.ltd.technolive.dao.imp.BoardCardListDao;
import com.appshole.ltd.technolive.dao.imp.BoardDao;
import com.appshole.ltd.technolive.model.Board;
import com.appshole.ltd.technolive.model.BoardCardList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class ListViewMyBoardCardsAdapter extends BaseAdapter {
    /**
     * init variable
     */
    private ArrayList<BoardCardList> boardCardListArrayList;
    public Context context;
    public LayoutInflater inflater;
    private onSelectedBoardCardListListener m_onSelectedBoardCardListListener;


    public ListViewMyBoardCardsAdapter(Context context, ArrayList<BoardCardList> boardCardListArrayList, onSelectedBoardCardListListener m_onSelectedBoardCardListListener) {
        super();
        this.context = context;
        this.boardCardListArrayList = boardCardListArrayList;
        this.m_onSelectedBoardCardListListener = m_onSelectedBoardCardListListener;
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


            convertView = inflater.inflate(R.layout.listview_my_board_cardlist, null);


            holder.txtCardname = (TextView) convertView.findViewById(R.id.txtCardname);

            holder.imgAttachImage = (ImageView) convertView.findViewById(R.id.imgAttachImage);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub


                    try {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        BoardCardList boardCardList = (BoardCardList) viewHolder.txtCardname.getTag();
                        m_onSelectedBoardCardListListener.onClick(1, boardCardList);
                    } catch (Exception e) {
                        e.getMessage();
                    }


                }
            });

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ViewHolder viewHolder = (ViewHolder) v.getTag();
                    BoardCardList boardCardList = (BoardCardList) viewHolder.txtCardname.getTag();
                    m_onSelectedBoardCardListListener.onClick(2, boardCardList);
                    return true;
                }
            });


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {


            final BoardCardList boardCardList = boardCardListArrayList.get(position);

            if(boardCardList.getAttachmentCoverId() != null &&  !boardCardList.getAttachmentCoverId().equals("null")){

                holder.imgAttachImage.setVisibility(View.VISIBLE);

                getAttachmentFileFromServer(boardCardList, holder.imgAttachImage);

            } else {

                holder.imgAttachImage.setVisibility(View.GONE);
            }

            holder.txtCardname.setText(boardCardList.getName() + "");
            holder.txtCardname.setTag(boardCardList);

            convertView.setTag(holder);

        } catch (Exception e) {
            Log.v("dd", e.toString());
        }

        return convertView;
    }

    private void getAttachmentFileFromServer(final BoardCardList boardCardList, final ImageView imgAttachImage) {


        AsyncHttpClient client = new AsyncHttpClient();


        String url = "https://api.trello.com/1/cards/"+boardCardList.getId()+"/attachments?fields=all&key=39615017597892d384d576145f2003c1";

        client.get(context, url, new JsonHttpResponseHandler() {



            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);


                try {
                    IBoardCardListDao boardDao = new BoardCardListDao(
                            context);


                    ArrayList<BoardCardList> boardArrayList = boardDao.GetBoardCardListFromJSONArray(response);


                    if (boardArrayList != null && boardArrayList.size() > 0) {


                        Glide.with(context)
                                .load(boardArrayList.get(0).getUrl())
                                .placeholder(R.drawable.card_cover_placeholder)
                                .crossFade().override(200, 200)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imgAttachImage);



                    }


                } catch (Exception ex) {
                    ex.getMessage();
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }


        });









    }


    public static class ViewHolder {

        private TextView txtCardname;

        private ImageView imgAttachImage;

    }

    public void setData(ArrayList<BoardCardList> boardCardListArrayList) {
        // TODO Auto-generated method stub
        this.boardCardListArrayList = boardCardListArrayList;
    }

    public interface onSelectedBoardCardListListener {
        void onClick(int type, BoardCardList myboardCardList);
    }


}