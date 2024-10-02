package com.example.springclient.model;

import com.google.gson.annotations.SerializedName;

public class OrderDetailsStatus {

    @SerializedName("id")
    private int id;
    @SerializedName("statusName")
    private String statusName;

    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
