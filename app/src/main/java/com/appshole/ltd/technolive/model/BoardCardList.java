package com.appshole.ltd.technolive.model;

import java.io.Serializable;

/**
 * Created by mrmamunurrahman on 8/29/16.
 */
public class BoardCardList implements Serializable {


    private String id;

    private String boardId;
    private String listId;
    private String AttachmentCoverId;
    private String name;
    private String closed;
    private String url;


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

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getAttachmentCoverId() {
        return AttachmentCoverId;
    }

    public void setAttachmentCoverId(String attachmentCoverId) {
        AttachmentCoverId = attachmentCoverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
