package com.appshole.ltd.technolive.dao.imp;

import android.content.Context;

import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardDao;
import com.appshole.ltd.technolive.model.Board;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BoardDao implements IBoardDao {
    public BoardDao(Context activity) {

    }


    @Override
    public ArrayList<Board> GetBoardFromJSONArray(JSONArray jsonArray) throws Exception {


        ArrayList<Board> boardArrayList = new ArrayList<Board>();

        Board board = null;


        for (int i = 0; i < jsonArray.length(); i++) {

            board = new Board();
            JSONObject jsonObject = jsonArray.getJSONObject(i);


            try {
                board.setIsClosed(jsonObject.getString("closed"));
            } catch (Exception ex) {
            }


            try {
                board.setBoardShortLink(jsonObject.getString("shortLink"));
            } catch (Exception ex) {
            }

            try {

                board.setBoardName(jsonObject.getString("name"));

            } catch (Exception ex) {
            }

            try {

                board.setBoardID(jsonObject.getString("id"));

            } catch (Exception ex) {
            }


            boardArrayList.add(board);
        }
        return boardArrayList;
    }
}
