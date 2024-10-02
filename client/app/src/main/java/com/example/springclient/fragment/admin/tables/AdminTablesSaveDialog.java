package com.example.springclient.fragment.admin.tables;

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
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.model.Tables;
import com.example.springclient.model.TablesStatus;

import java.util.List;

public class AdminTablesSaveDialog extends DialogFragment {

    private EditText editTextTableNumber;
    private AdminTablesViewModel adminTablesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dialog_tables_save, container, false);

        editTextTableNumber = view.findViewById(R.id.editTextTableNumber);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);

        adminTablesViewModel = new ViewModelProvider(requireActivity()).get(AdminTablesViewModel.class);

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonAdd.setOnClickListener(v -> {
            String tableNumberStr = editTextTableNumber.getText().toString().trim();
            if (tableNumberStr.isEmpty()) {
                Toast.makeText(getContext(), "Введите номер стола", Toast.LENGTH_SHORT).show();
                return;
            }

            int tableNumber = Integer.parseInt(tableNumberStr);

            if (isTableNumberExists(tableNumber)) {
                Toast.makeText(getContext(), "Такой стол уже существует", Toast.LENGTH_SHORT).show();
                return;
            }
            addTable(tableNumber);
        });

        return view;
    }

    private boolean isTableNumberExists(int tableNumber) {
        List<Tables> tablesList = adminTablesViewModel.getTablesList().getValue();
        Log.d("TableCheck", "Размер" + tablesList.size());
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

    private void addTable(int tableNumber) {
        List<TablesStatus> statuses = adminTablesViewModel.getTablesStatusesList().getValue();
        if (statuses != null) {
            for (TablesStatus status : statuses) {
                if ("Свободен".equals(status.getStatusName())) {
                    Tables newTable = new Tables();
                    newTable.setTableNumber(tableNumber);
                    newTable.setStatus(status);

                    adminTablesViewModel.saveTable(newTable).observe(this, response -> {
                        if (response != null && response.isSuccessful()) {
                            Toast.makeText(getContext(), "Стол добавлен", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Ошибка при добавлении стола", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
            }
        } else {
            Toast.makeText(getContext(), "Ошибка при загрузке статусов", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Добавить стол");
        return dialog;
    }
}