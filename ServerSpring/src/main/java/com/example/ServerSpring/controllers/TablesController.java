package com.example.ServerSpring.controllers;

import com.example.ServerSpring.model.Table_Status.TablesStatus;
import com.example.ServerSpring.model.Table_Status.TablesStatusDao;
import com.example.ServerSpring.model.Tables.Tables;
import com.example.ServerSpring.model.Tables.TablesDao;
import com.example.ServerSpring.webSocket.GenericWebSocketHandler;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TablesController {
    @Autowired
    private TablesDao tablesDao;

    @Autowired
    private TablesStatusDao tablesStatusDao;

    @Autowired
    private GenericWebSocketHandler webSocketHandler;

    @GetMapping("/tables/get-all")
    public List<Tables> getAllTables(){
        return tablesDao.getAllTables();
    }

    @GetMapping("/tables/status/get-all")
    public List<TablesStatus> getAllTablesStatus(){
        return tablesStatusDao.getAllTablesStatus();
    }

    @PostMapping("/tables")
    public Tables save (@RequestBody Tables table){
        Tables tableSave = tablesDao.tableSave(table);
        Gson gson = new Gson();
        String tableJson = gson.toJson(tableSave);
        webSocketHandler.broadcastMessage("TABLE_ADDED:" + tableJson);
        return tableSave;
    }

    @DeleteMapping("/tables/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable("id") int tableId) {
        try {
            Tables table = tablesDao.findById(tableId);
            Gson gson = new Gson();
            String tableJson = gson.toJson(table);
            tablesDao.tableDelete(tableId);
            webSocketHandler.broadcastMessage("TABLE_DELETED:" + tableJson);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/tables/{id}")
    public ResponseEntity<Tables> updateTable(@PathVariable("id") int tableId, @RequestBody Tables table) {

        table.setId(tableId);

        Tables updatedTable = tablesDao.tableUpdate(table);
        Gson gson = new Gson();
        String tableJson = gson.toJson(updatedTable);

        if (updatedTable != null) {
            webSocketHandler.broadcastMessage("TABLE_UPDATED:" + tableJson);
            return ResponseEntity.ok(updatedTable);
        } else {
            return ResponseEntity.notFound().build();
    }
}