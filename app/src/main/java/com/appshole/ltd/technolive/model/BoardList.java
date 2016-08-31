package com.appshole.ltd.technolive.model;

import java.util.ArrayList;

/**
 * Created by mrmamunurrahman on 8/29/16.
 */
public class BoardList {

    private String name;
    private String id;
    private String boardId;


    public ArrayList<BoardCardList>boardCardListArrayList = new ArrayList<>();


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }
}
