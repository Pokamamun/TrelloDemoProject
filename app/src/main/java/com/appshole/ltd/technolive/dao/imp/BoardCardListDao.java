package com.appshole.ltd.technolive.dao.imp;

import android.content.Context;

import com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao.IBoardCardListDao;
import com.appshole.ltd.technolive.model.BoardCardList;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BoardCardListDao implements IBoardCardListDao {
    public BoardCardListDao(Context activity) {
    }


    @Override
    public ArrayList<BoardCardList> GetBoardCardListFromJSONObject(JSONObject jsonObject) throws Exception {
        ArrayList<BoardCardList> boardArrayList = new ArrayList<BoardCardList>();

        BoardCardList boardCardList = null;


        boardCardList = new BoardCardList();

        try {
            boardCardList.setUrl(jsonObject.getString("url"));
        } catch (Exception ex) {
        }

        try {
            boardCardList.setId(jsonObject.getString("id"));
        } catch (Exception ex) {
        }

        try {

            boardCardList.setName(jsonObject.getString("name"));

        } catch (Exception ex) {
        }

        try {

            boardCardList.setAttachmentCoverId(jsonObject.getString("idAttachmentCover"));

        } catch (Exception ex) {
        }


        try {

            boardCardList.setBoardId(jsonObject.getString("idBoard"));

        } catch (Exception ex) {
        }


        try {

            boardCardList.setClosed(jsonObject.getString("closed"));

        } catch (Exception ex) {
        }


        try {

            boardCardList.setListId(jsonObject.getString("idList"));

        } catch (Exception ex) {
        }

        boardArrayList.add(boardCardList);
        // }
        return boardArrayList;
    }

    @Override
    public ArrayList<BoardCardList> GetBoardCardListFromJSONArray(JSONArray jsonArray) throws Exception {

        ArrayList<BoardCardList> boardArrayList = new ArrayList<BoardCardList>();

        BoardCardList boardCardList = null;


        for (int i = 0; i < jsonArray.length(); i++) {

            boardCardList = new BoardCardList();
            JSONObject jsonObject = jsonArray.getJSONObject(i);


            try {
                boardCardList.setId(jsonObject.getString("id"));
            } catch (Exception ex) {
            }

            try {

                boardCardList.setName(jsonObject.getString("name"));

            } catch (Exception ex) {
            }

            try {

                boardCardList.setAttachmentCoverId(jsonObject.getString("idAttachmentCover"));

            } catch (Exception ex) {
            }


            try {

                boardCardList.setBoardId(jsonObject.getString("idBoard"));

            } catch (Exception ex) {
            }


            try {

                boardCardList.setClosed(jsonObject.getString("closed"));

            } catch (Exception ex) {
            }


            try {

                boardCardList.setListId(jsonObject.getString("idList"));

            } catch (Exception ex) {
            }


            try {

                boardCardList.setUrl(jsonObject.getString("url"));

            } catch (Exception ex) {
            }



            boardArrayList.add(boardCardList);
        }
        return boardArrayList;
    }
}
