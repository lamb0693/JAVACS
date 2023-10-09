package com.example.receiver;

public class ResponseBoardList {
    private Long board_id;
    private String name;
    private String content;
    private String message;
    private String  strUpdatedAt;
    
    public ResponseBoardList() {
    }

    public Long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Long board_id) {
        this.board_id = board_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUpdate_date() {
        return strUpdatedAt;
    }

    public void setUpdate_date(String update_date) {
        this.strUpdatedAt = update_date;
    }

}
