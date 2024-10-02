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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.springclient.MainActivity;
import com.example.springclient.R;
import com.example.springclient.fragment.admin.categories.AdminCategoriesViewModel;
import com.example.springclient.model.Categories;
import com.example.springclient.model.Dishes;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AdminDishesEditFragment extends Fragment {

    private AdminDishesViewModel adminDishesViewModel;
    private AdminCategoriesViewModel adminCategoriesViewModel;
    private Dishes dish;
    private Spinner spinnerCategory;
    private Spinner spinnerPlaceCooking;
    private CheckBox checkBoxAvailability;
    private List<Categories> categoriesList;
    private TextInputEditText inputEditTextName;
    private TextInputEditText inputEditTextCost;
    private TextInputEditText inputEditTextDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_dishes_edit, container, false);
        adminDishesViewModel = new ViewModelProvider(this).get(AdminDishesViewModel.class);
        adminCategoriesViewModel = new ViewModelProvider(this).get(AdminCategoriesViewModel.class);
        initializeComponents(view);
        loadCategories();
        return view;
    }

    private void initializeComponents(View view) {
        inputEditTextName = view.findViewById(R.id.form_textFieldDishesName);
        inputEditTextCost = view.findViewById(R.id.form_textFieldDishesCost);
        inputEditTextDescription = view.findViewById(R.id.form_textFieldDishesDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerPlaceCooking = view.findViewById(R.id.spinnerPlaceCooking);
        checkBoxAvailability = view.findViewById(R.id.checkBoxAvailability);
        Button buttonDishesSave = view.findViewById(R.id.dishes_edit_button);

        Toolbar toolbar = view.findViewById(R.id.toolbar_dish);
        toolbar.setNavigationIcon(R.drawable.ic_back); // assuming you have this icon
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setTitle("Редактировать блюдо " + dish.getName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        ArrayAdapter<CharSequence> adapterPlaceCooking = ArrayAdapter.createFromResource(
                getContext(),
                R.array.place_cooking_array,
                android.R.layout.simple_spinner_item
        );
        adapterPlaceCooking.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaceCooking.setAdapter(adapterPlaceCooking);

        if (dish != null) {
            inputEditTextName.setText(dish.getName());
            inputEditTextCost.setText(String.valueOf(dish.getCost()));
            inputEditTextDescription.setText(dish.getDescription());
            checkBoxAvailability.setChecked(dish.getAvailability());

            // Установить значение Spinner для места приготовления
            if (dish.getPlaceCooking() != null) {
                int spinnerPosition = adapterPlaceCooking.getPosition(dish.getPlaceCooking());
                spinnerPlaceCooking.setSelection(spinnerPosition);
            }
        }

        buttonDishesSave.setOnClickListener(v -> {
            String newName = String.valueOf(inputEditTextName.getText());
            String newCostText = String.valueOf(inputEditTextCost.getText());
            String newDescription = String.valueOf(inputEditTextDescription.getText());
            String newPlaceCooking = spinnerPlaceCooking.getSelectedItem().toString();
            boolean availability = checkBoxAvailability.isChecked();

            if (newName.isEmpty() || newCostText.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(getContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            int newCost;
            try {
                newCost = Integer.parseInt(newCostText);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Стоимость должна быть числом", Toast.LENGTH_SHORT).show();
                return;
            }

            Categories selectedCategory = (Categories) spinnerCategory.getSelectedItem();
            if (selectedCategory == null) {
                Toast.makeText(getContext(), "Выберите категорию", Toast.LENGTH_SHORT).show();
                return;
            }

            dish.setName(newName);
            dish.setCost(newCost);
            dish.setDescription(newDescription);
            dish.setCategory(selectedCategory);
            dish.setPlaceCooking(newPlaceCooking);
            dish.setAvailability(availability);

            adminDishesViewModel.updateDish(dish).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Блюдо успешно обновлено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Не удалось обновить блюдо", Toast.LENGTH_SHORT).show();
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

                    if (dish != null && dish.getCategory() != null) {
                        for (int i = 0; i < categoriesList.size(); i++) {
                            if (categoriesList.get(i).getId() == dish.getCategory().getId()) {
                                spinnerCategory.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
        });
        adminCategoriesViewModel.loadCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Изменить блюдо");
        }
    }

    // Метод для передачи блюда из адаптера
    public void setDish(Dishes dish) {
        this.dish = dish;
    }
}