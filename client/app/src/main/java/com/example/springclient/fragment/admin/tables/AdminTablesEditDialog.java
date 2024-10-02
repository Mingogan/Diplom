package com.example.springclient.fragment.admin.tables;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.model.Tables;
import com.example.springclient.model.TablesStatus;

import java.util.List;


public class AdminTablesEditDialog extends DialogFragment {

    private EditText editTextTableNumber;
    private Spinner spinnerStatus;
    private AdminTablesViewModel adminTablesViewModel;
    private Tables table;

    public AdminTablesEditDialog(Tables table) {
        this.table = table;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dialog_tables_edit, container, false);

        editTextTableNumber = view.findViewById(R.id.editTextTableNumber);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);

        adminTablesViewModel = new ViewModelProvider(requireActivity()).get(AdminTablesViewModel.class);

        editTextTableNumber.setText(String.valueOf(table.getTableNumber()));

        adminTablesViewModel.getTablesStatusesList().observe(getViewLifecycleOwner(), statuses -> {
            if (statuses != null) {
                SpinerStatusAdapter adapter = new SpinerStatusAdapter(getContext(), android.R.layout.simple_spinner_item, statuses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatus.setAdapter(adapter);

                int position = adapter.getPosition(table.getStatus());
                spinnerStatus.setSelection(position);
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonDelete.setOnClickListener(v -> {
            adminTablesViewModel.deleteTable(table.getId());
            dismiss();
        });

        buttonUpdate.setOnClickListener(v -> {
            String tableNumberStr = editTextTableNumber.getText().toString().trim();
            if (tableNumberStr.isEmpty()) {
                Toast.makeText(getContext(), "Введите номер стола", Toast.LENGTH_SHORT).show();
                return;
            }

            int tableNumber = Integer.parseInt(tableNumberStr);
            TablesStatus selectedStatus = (TablesStatus) spinnerStatus.getSelectedItem();
            if (isTableNumberExists(tableNumber)) {
                Toast.makeText(getContext(), "Такой стол уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            table.setTableNumber(tableNumber);
            table.setStatus(selectedStatus);

            adminTablesViewModel.updateTable(table).observe(this, response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Стол обновлен", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Ошибка при обновлении стола", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private boolean isTableNumberExists(int tableNumber) {
        adminTablesViewModel.loadTables();
        List<Tables> tablesList = adminTablesViewModel.getTablesList().getValue();
        Log.d("TableCheck", "Размер" + tablesList.size());
        if (table.getTableNumber() == tableNumber) {
            return false;
        }
        if (tablesList != null) {
            for (Tables table : tablesList) {
                if (table.getTableNumber() == tableNumber) {
                    return true;
                }
            }
        }
        Log.d("TableCheck", "Список столов null");
        return false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Редактировать стол");
        return dialog;
    }
}
