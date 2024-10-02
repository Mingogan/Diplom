package com.example.springclient.fragment.admin.categories;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.springclient.MainActivity;
import com.example.springclient.R;

import com.example.springclient.model.Categories;
import com.example.springclient.retrofit.ServerApi;
import com.google.android.material.textfield.TextInputEditText;

public class AdminCategoriesEditFragment extends Fragment {

    private AdminCategoriesViewModel adminCategoriesViewModel;
    private Categories category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_categories_save, container, false); // Используем тот же макет
        adminCategoriesViewModel = new ViewModelProvider(this).get(AdminCategoriesViewModel.class);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view){
        TextInputEditText inputEditText = view.findViewById(R.id.form_textFieldCategories);
        Button buttonCategoriesSave = view.findViewById(R.id.categories_save_button);

        if (category != null) {
            inputEditText.setText(category.getName());
        }

        buttonCategoriesSave.setOnClickListener(v -> {
            String newName = String.valueOf(inputEditText.getText());
            category.setName(newName);

            adminCategoriesViewModel.updateCategory(category).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Категория успешно обновлена", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Не удалось обновить категорию", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Изменить категорию");
        }
    }

    public void setCategory(Categories category) {
        this.category = category;
    }
}