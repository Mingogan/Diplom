package com.example.ServerSpring.model.Tables;
import com.example.ServerSpring.model.Table_Status.TablesStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tables", schema = "public", catalog = "Diplom")
public class Tables {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "table_number")
    private int tableNumber;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private TablesStatus status;

    // Getters and Setters
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
