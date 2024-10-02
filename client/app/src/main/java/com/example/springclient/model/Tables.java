package com.example.springclient.model;

public class Tables {
    private int id;
    private int tableNumber;
    private TablesStatus status;

    public Tables() {
    }

    public Tables(int id, int tableNumber, TablesStatus status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public TablesStatus getStatus() {
        return status;
    }

    public void setStatus(TablesStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Tables{" +
                "id=" + id +
                ", tableNumber=" + tableNumber +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tables that = (Tables) o;

        if (id != that.id) return false;
        if (tableNumber != that.tableNumber) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + tableNumber;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}