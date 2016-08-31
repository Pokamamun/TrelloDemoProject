package com.appshole.ltd.technolive.dao.imp;

import android.content.Context;


import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardListDao;
import com.appshole.ltd.technolive.model.BoardList;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BoardListDao implements IBoardListDao {
    public BoardListDao(Context activity) {
    }


    @Override
    public ArrayList<BoardList> GetBoardListDeleteFromJSONObject(JSONObject jsonObject) throws Exception {
        ArrayList<BoardList> boardListArrayList = new ArrayList<BoardList>();

        BoardList boardList = new BoardList();
        try {


            try {
                boardList.setId(jsonObject
                        .getString("id"));


            } catch (Exception ex) {
            }


            try {
                boardList.setBoardId(jsonObject
                        .getString("idBoard"));
            } catch (Exception ex) {
            }

            try {

                boardList.setName(jsonObject.getString("name"));
            } catch (Exception ex) {
            }


            boardListArrayList.add(boardList);

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return boardListArrayList;
    }

    @Override
    public ArrayList<BoardList> GetBoardListFromJSONObject(JSONObject json) throws Exception {

        ArrayList<BoardList> boardListArrayList = new ArrayList<BoardList>();

        BoardList boardList = null;
        try {

            String boardid = json.getString("id");

            JSONArray jArray = json.getJSONArray("lists");

            for (int i = 0; i < jArray.length(); i++) {
                boardList = new BoardList();

                json = jArray.getJSONObject(i);


                try {
                    boardList.setId(json
                            .getString("id"));
                } catch (Exception ex) {
                }


                try {

                    boardList.setName(json.getString("name"));
                } catch (Exception ex) {
                }


                boardList.setBoardId(boardid);
                boardListArrayList.add(boardList);
            }
        } catch (JSONException ex) {
            throw new Exception(ex.getMessage());
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return boardListArrayList;


    }


}
