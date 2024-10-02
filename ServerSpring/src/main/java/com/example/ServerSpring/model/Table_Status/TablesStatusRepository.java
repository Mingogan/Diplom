package com.example.ServerSpring.model.Table_Status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablesStatusRepository extends JpaRepository<TablesStatus, Integer> {
}
