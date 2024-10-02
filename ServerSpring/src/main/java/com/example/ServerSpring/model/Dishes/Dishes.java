package com.example.ServerSpring.model.Dishes;

import jakarta.persistence.*;
import com.example.ServerSpring.model.Categorys.*;

@Entity
@Table(name = "dishes", schema = "public", catalog = "Diplom")
public class Dishes {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "cost")
    private int cost;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Categories category;

    @Column(name = "placeCooking")
    private String placeCooking;

    @Column(name = "availability")
    private Boolean availability;
    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public String getPlaceCooking() {
        return placeCooking;
    }

    public void setPlaceCooking(String placeCooking) {
        this.placeCooking = placeCooking;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dishes that = (Dishes) o;

        if (id != that.id) return false;
        if (cost != that.cost) return false;
        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + cost;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}