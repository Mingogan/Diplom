package com.example.springclient.fragment.waiter.tables;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.waiter.WaiterMainFragment;
import com.example.springclient.fragment.waiter.table_orders.WaiterTableOrdersFragment;
import com.example.springclient.model.Tables;
import com.example.springclient.model.Users;
import com.example.springclient.retrofit.ServerApi;
import com.google.android.material.button.MaterialButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaiterTablesAdapter extends BaseAdapter {
    private Context context;
    private List<Tables> tablesList;
    private LayoutInflater inflater;
    private WaiterTablesViewModel waiterTablesViewModel;

    private LifecycleOwner lifecycleOwner;

    public WaiterTablesAdapter(Context context, List<Tables> tablesList, WaiterTablesViewModel waiterTablesViewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.tablesList = tablesList;
        this.inflater = LayoutInflater.from(context);
        this.waiterTablesViewModel = waiterTablesViewModel;
        this.lifecycleOwner = lifecycleOwner;
        sortTablesList();
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
        Button tableButton = convertView.findViewById(R.id.table_button);
        tableButton.setText(String.valueOf(table.getTableNumber()));

        setButtonStyle(tableButton, table.getStatus().getStatusName());

        setOnClickListener(convertView, table);

        return convertView;
    }

    private void setButtonStyle(Button button, String statusName) {
        if ("Свободен".equals(statusName)) {
            button.setBackgroundTintList(context.getResources().getColorStateList(R.color.toolbar_background));
            button.setTextColor(context.getResources().getColor(R.color.white));
        } else if ("Занят".equals(statusName)) {
            button.setBackgroundTintList(context.getResources().getColorStateList(R.color.dark_gray));
            button.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    private void setOnClickListener(View view, Tables table) {
        view.setOnClickListener(v -> {
            if ("Свободен".equals(table.getStatus().getStatusName())) {
                showFreeTableDialog(table);
            } else if ("Занят".equals(table.getStatus().getStatusName())) {
                showOccupiedTableDialog(table);
            }
        });
    }

    private void showFreeTableDialog(Tables table) {
        new AlertDialog.Builder(context)
                .setTitle("Новый заказ")
                .setMessage("Стол №" + table.getTableNumber() + " свободен. Вы хотите начать новый заказ на нём?")
                .setPositiveButton("Да", (dialog, which) -> {
                    waiterTablesViewModel.saveOrder(table.getId(), MyApplication.getUser().getId());
                    navigateToTableOrdersFragment(table.getId(),table.getTableNumber());
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void showOccupiedTableDialog(Tables table) {
        new AlertDialog.Builder(context)
                .setTitle("Заказ")
                .setMessage("Стол №" + table.getTableNumber() + " уже занят. Вы хотите перейти к окну его заказов?")
                .setPositiveButton("Да", (dialog, which) -> {
                    waiterTablesViewModel.findWaiterByTableId(table.getId());
                    waiterTablesViewModel.getWaiter().observe(lifecycleOwner, new Observer<Users>() {
                        @Override
                        public void onChanged(Users waiter) {
                            if (waiter != null) {
                                navigateOccupiedToTableOrdersFragment(waiter, table.getId(),table.getTableNumber());
                                waiterTablesViewModel.getWaiter().removeObserver(this); // Remove observer after handling the data
                                waiterTablesViewModel.resetWaiter(); // Reset LiveData value to avoid automatic navigation on re-entry
                            }
                        }
                    });
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void navigateToTableOrdersFragment(int tableId, int tableNumber) {
        FragmentActivity activity = (FragmentActivity) context;
        WaiterTableOrdersFragment fragment = WaiterTableOrdersFragment.newInstance(MyApplication.getUser(),tableId, tableNumber);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateOccupiedToTableOrdersFragment(Users waiter, int tableId,int tableNumber) {
        FragmentActivity activity = (FragmentActivity) context;
        WaiterTableOrdersFragment fragment = WaiterTableOrdersFragment.newInstance(waiter,tableId,tableNumber);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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
            //setOnClickListener(view, table);  // Установка слушателя нажатий
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