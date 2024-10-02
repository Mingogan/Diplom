package com.example.ServerSpring.model.Tables;

import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.Roles.Roles;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TablesDao {
    @Autowired
    private TablesRepository tablesRepository;

    @Transactional
    public Tables tableSave(Tables tables){
        return tablesRepository.save(tables);
    }

    public Tables findById(int id) {
        return tablesRepository.findById(id).orElse(null);
    }

    public void tableDelete(int tableId) {
        if(tablesRepository.existsById(tableId)) {
            tablesRepository.deleteById(tableId);
        } else {
            throw new EntityNotFoundException("Table not found");
        }
    }
    public List<Tables> getAllTables(){
        List<Tables> tablesList = new ArrayList<>();
        Streamable.of(tablesRepository.findAll())
                .forEach(tables -> {
                    tablesList.add(tables);
                });
        return tablesList;
    }

    public Tables tableUpdate(Tables table) {
        if(tablesRepository.existsById(table.getId())) {
            return tablesRepository.save(table);
        } else {
            throw new EntityNotFoundException("Table not found");
        }
    }
}
