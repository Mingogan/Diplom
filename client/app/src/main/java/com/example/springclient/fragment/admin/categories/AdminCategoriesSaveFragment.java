package com.example.springclient.fragment.admin.categories;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.MainActivity;

import com.example.springclient.model.Categories;
import com.google.android.material.textfield.TextInputEditText;

public class AdminCategoriesSaveFragment extends Fragment {

    private AdminCategoriesViewModel adminCategoriesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_categories_save, container, false);
        adminCategoriesViewModel = new ViewModelProvider(this).get(AdminCategoriesViewModel.class);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view){
        TextInputEditText inputEditText = view.findViewById(R.id.form_textFieldCategories);
        Button buttonCategoriesSave = view.findViewById(R.id.categories_save_button);

        buttonCategoriesSave.setOnClickListener(v -> {
            String name = String.valueOf(inputEditText.getText());
            Categories category = new Categories();
            category.setName(name);

            adminCategoriesViewModel.saveCategory(category).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Категория успешно добавлена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Не удалось добавить категорию", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Сохранить категорию");
        }
    }
}
