package com.example.springclient.fragment.admin.dishes;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.MainActivity;
import com.example.springclient.fragment.admin.categories.AdminCategoriesViewModel;
import com.example.springclient.model.Categories;
import com.example.springclient.model.Dishes;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AdminDishesSaveFragment extends Fragment {

    private AdminDishesViewModel adminDishesViewModel;
    private AdminCategoriesViewModel adminCategoriesViewModel;
    private Spinner spinnerCategory;
    private Spinner spinnerPlaceCooking;
    private List<Categories> categoriesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_dishes_save, container, false);
        adminDishesViewModel = new ViewModelProvider(this).get(AdminDishesViewModel.class);
        adminCategoriesViewModel = new ViewModelProvider(this).get(AdminCategoriesViewModel.class);
        initializeComponents(view);
        loadCategories();
        return view;
    }

    private void initializeComponents(View view) {
        TextInputEditText inputEditTextName = view.findViewById(R.id.form_textFieldDishesName);
        TextInputEditText inputEditTextCost = view.findViewById(R.id.form_textFieldDishesCost);
        TextInputEditText inputEditTextDescription = view.findViewById(R.id.form_textFieldDishesDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPlaceCooking = view.findViewById(R.id.spinnerPlaceCooking);
        Button buttonDishesSave = view.findViewById(R.id.dishes_save_button);

        Toolbar toolbar = view.findViewById(R.id.toolbar_dish);
        toolbar.setNavigationIcon(R.drawable.ic_back); // assuming you have this icon
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setTitle("Добавить блюдо ");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupToolbar(toolbar);
        }

        ArrayAdapter<CharSequence> adapterPlaceCooking = ArrayAdapter.createFromResource(
                getContext(),
                R.array.place_cooking_array,
                android.R.layout.simple_spinner_item
        );
        adapterPlaceCooking.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaceCooking.setAdapter(adapterPlaceCooking);

        buttonDishesSave.setOnClickListener(v -> {
            String name = String.valueOf(inputEditTextName.getText());
            String costText = String.valueOf(inputEditTextCost.getText());
            String description = String.valueOf(inputEditTextDescription.getText());
            String placeCooking = spinnerPlaceCooking.getSelectedItem().toString();

            if (name.isEmpty() || costText.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            int cost = Integer.parseInt(costText);
            Categories selectedCategory = (Categories) spinnerCategory.getSelectedItem();

            Dishes dish = new Dishes();
            dish.setName(name);
            dish.setCost(cost);
            dish.setDescription(description);
            dish.setCategory(selectedCategory);
            dish.setAvailability(true);
            dish.setPlaceCooking(placeCooking);

            adminDishesViewModel.saveDish(dish).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Блюдо успешно добавлено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Не удалось добавить блюдо", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadCategories() {
        adminCategoriesViewModel.getCategoriesList().observe(getViewLifecycleOwner(), new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                if (categories != null) {
                    categoriesList = categories;
                    ArrayAdapter<Categories> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoriesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }
            }
        });
        adminCategoriesViewModel.loadCategories();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Сохранить блюдо");
        }
    }
}