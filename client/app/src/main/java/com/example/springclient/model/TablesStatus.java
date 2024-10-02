package com.example.springclient.model;

public class TablesStatus {
    private int id;
    private String statusName;

    public TablesStatus() {
    }

    public TablesStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "TableStatus{" +
                "id=" + id +
                ", statusName='" + statusName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TablesStatus that = (TablesStatus) o;

        if (id != that.id) return false;
        return statusName != null ? statusName.equals(that.statusName) : that.statusName == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (statusName != null ? statusName.hashCode() : 0);
        return result;
    }
}