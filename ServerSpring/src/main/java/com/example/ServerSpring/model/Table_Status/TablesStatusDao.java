package com.example.ServerSpring.model.Table_Status;

import com.example.ServerSpring.model.Users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TablesStatusDao {
    @Autowired
    private TablesStatusRepository tablesStatusRepository;

    public List<TablesStatus> getAllTablesStatus(){
        List<TablesStatus> tablesList = new ArrayList<>();
        Streamable.of(tablesStatusRepository.findAll())
                .forEach(tablesStatus -> {
                    tablesList.add(tablesStatus);
                });
        return tablesList;
    }

    public TablesStatus findById(int id) {
        return tablesStatusRepository.findById(id).orElse(null);
    }
}