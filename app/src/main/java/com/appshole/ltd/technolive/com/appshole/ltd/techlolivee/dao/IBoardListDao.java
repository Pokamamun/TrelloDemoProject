package com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao;

import com.appshole.ltd.technolive.model.BoardList;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public interface IBoardListDao {


	ArrayList<BoardList> GetBoardListDeleteFromJSONObject(JSONObject json) throws Exception;
	ArrayList<BoardList> GetBoardListFromJSONObject(JSONObject json) throws Exception;





}
