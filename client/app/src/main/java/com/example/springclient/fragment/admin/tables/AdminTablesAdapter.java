package com.example.springclient.fragment.admin.tables;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.fragment.app.FragmentActivity;

import com.example.springclient.R;
import com.example.springclient.model.Tables;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminTablesAdapter extends BaseAdapter {
    private Context context;
    private List<Tables> tablesList;
    private LayoutInflater inflater;
    private AdminTablesViewModel adminTablesViewModel;

    public AdminTablesAdapter(Context context, List<Tables> tablesList, AdminTablesViewModel adminTablesViewModel) {
        this.context = context;
        this.tablesList = tablesList;
        this.inflater = LayoutInflater.from(context);
        this.adminTablesViewModel= adminTablesViewModel;
    }

    @Override
    public int getCount() {
        return tablesList.size();
    }

    @Override
    public Object getItem(int position) {
        return tablesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tablesList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_tables, parent, false);
        }

        Tables table = tablesList.get(position);
        MaterialButton tableButton = convertView.findViewById(R.id.table_button);
        tableButton.setText(String.valueOf(table.getTableNumber()));

        setButtonStyle(tableButton, table.getStatus().getStatusName());


        convertView.setOnClickListener(v -> {
            AdminTablesEditDialog dialog = new AdminTablesEditDialog(table);
            dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "EditTableDialog");
        });

        return convertView;
    }

    public void setButtonStyle(MaterialButton button, String statusName) {
        if ("Свободен".equals(statusName)) {

        } else if ("Занят".equals(statusName)) {
            button.setBackgroundTintList(context.getResources().getColorStateList(R.color.teal_700));
            button.setTextColor(context.getResources().getColor(R.color.black));
        }
    }



    public void updateTablesList(List<Tables> newTablesList) {
        this.tablesList = newTablesList;
        sortTablesList();
        notifyDataSetChanged();
    }

    public void removeTableById(int id) {
        tablesList.removeIf(table -> table.getId() == id);
        sortTablesList();
        notifyDataSetChanged();
    }

    public void addTable(Tables table) {
        tablesList.add(table);
        sortTablesList();
        notifyDataSetChanged();
    }

    public void updateTable(Tables table) {
        for (int i = 0; i < tablesList.size(); i++) {
            if (tablesList.get(i).getId() == table.getId()) {
                tablesList.set(i, table);
                sortTablesList();
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (int i = 0; i < tablesList.size(); i++) {
            Tables table = tablesList.get(i);
            View view = getView(i, null, null);  // Создаем новый вид для каждой кнопки
            MaterialButton tableButton = view.findViewById(R.id.table_button);
            setButtonStyle(tableButton, table.getStatus().getStatusName());
            //setOnClickListener(view, table);
        }
    }
    private void sortTablesList() {
        Collections.sort(tablesList, new Comparator<Tables>() {
            @Override
            public int compare(Tables t1, Tables t2) {
                return Integer.compare(t1.getTableNumber(), t2.getTableNumber());
            }
        });
    }
}
