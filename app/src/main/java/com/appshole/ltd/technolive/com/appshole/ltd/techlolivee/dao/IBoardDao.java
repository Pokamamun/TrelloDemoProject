package com.appshole.ltd.technolive.com.appshole.ltd.techlolivee.dao;

import com.appshole.ltd.technolive.model.Board;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public interface IBoardDao {
	ArrayList<Board> GetBoardFromJSONArray(JSONArray json) throws Exception;




}
