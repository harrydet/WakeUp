package com.example.harry.wakeup;

import java.util.ArrayList;

/**
 * Created by Harry on 05/04/2015.
 */
public class TaskList {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String listName;
    private String listSubText;

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getListSubText() {
        return listSubText;
    }

    public void setListSubText(String listSubText) {
        this.listSubText = listSubText;
    }

    public TaskList(){
    }

}
