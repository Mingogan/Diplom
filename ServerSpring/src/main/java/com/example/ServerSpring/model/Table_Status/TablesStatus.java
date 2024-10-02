package com.example.ServerSpring.model.Table_Status;



import jakarta.persistence.*;

@Entity
@Table(name = "tables_status", schema = "public", catalog = "Diplom")
public class TablesStatus {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "status_name")
    private String statusName;

    // Getters and Setters
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
}
