package com.appshole.ltd.technolive.model;

/**
 * Created by mrmamunurrahman on 8/29/16.
 */
public class Board {

    private String boardName;
    private String boardID;
    private String boardShortLink;
    private String isClosed;



    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardID() {
        return boardID;
    }

    public void setBoardID(String boardID) {
        this.boardID = boardID;
    }

    public String getBoardShortLink() {
        return boardShortLink;
    }

    public void setBoardShortLink(String boardShortLink) {
        this.boardShortLink = boardShortLink;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(String isClosed) {
        this.isClosed = isClosed;
    }
}
