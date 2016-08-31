package com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao;

import com.appshole.ltd.technolive.model.BoardCardList;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public interface IBoardCardListDao {


	ArrayList<BoardCardList> GetBoardCardListFromJSONObject(JSONObject json) throws Exception;
	ArrayList<BoardCardList> GetBoardCardListFromJSONArray(JSONArray json) throws Exception;




}
