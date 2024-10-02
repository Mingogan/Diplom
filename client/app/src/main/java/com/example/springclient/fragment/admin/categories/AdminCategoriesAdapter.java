package com.example.springclient.fragment.admin.categories;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.springclient.MainActivity;
import com.example.springclient.R;
import com.example.springclient.model.Categories;
import com.google.android.material.button.MaterialButton;


import java.util.List;

public class AdminCategoriesAdapter extends ArrayAdapter<Categories> {

    private Context context;
    private List<Categories> categoriesList;
    private AdminCategoriesViewModel adminCategoriesViewModel;

    public AdminCategoriesAdapter(Context context, List<Categories> categoriesList, AdminCategoriesViewModel adminCategoriesViewModel) {
        super(context, R.layout.admin_list_item_categories, categoriesList);
        this.context = context;
        this.categoriesList = categoriesList;
        this.adminCategoriesViewModel = adminCategoriesViewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_list_item_categories, parent, false);
        }

        Categories category = categoriesList.get(position);

        TextView textViewCategoryName = convertView.findViewById(R.id.textViewCategoryName);
        MaterialButton buttonDelete = convertView.findViewById(R.id.buttonDelete);
        MaterialButton buttonEdit = convertView.findViewById(R.id.buttonEdit);

        textViewCategoryName.setText(category.getName());
        buttonDelete.setOnClickListener(v -> {
            adminCategoriesViewModel.deleteCategory(category.getId());
        });

        buttonEdit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                AdminCategoriesEditFragment editFragment = new AdminCategoriesEditFragment();
                editFragment.setCategory(category);
                ((MainActivity) context).loadFragment(editFragment, "Изменить категорию");
            }
        });

        return convertView;
    }



    public void updateCategoriesList(List<Categories> newCategoriesList) {
        categoriesList.clear();
        categoriesList.addAll(newCategoriesList);
        notifyDataSetChanged();
    }

    public void removeCategoryById(int categoryId) {
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).getId() == categoryId) {
                categoriesList.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }
    public void addCategoryById(Categories category) {
        categoriesList.add(category);
        notifyDataSetChanged();
    }
    public void updateCategory(Categories category) {
        int index = findCategoryIndexById(category.getId());
        if (index != -1) {
            categoriesList.set(index, category);
        }
        notifyDataSetChanged();
    }

    private int findCategoryIndexById(int id) {
        for (int i = 0; i < categoriesList.size(); i++) {
            if (categoriesList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }


}