package com.example.springclient.fragment.admin.dishes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.example.springclient.MainActivity;
import com.example.springclient.R;
import com.example.springclient.model.Dishes;

import java.util.List;

public class AdminDishesAdapter extends ArrayAdapter<Dishes> {

    private Context context;
    private List<Dishes> dishesList;
    private AdminDishesViewModel adminDishesViewModel;

    public AdminDishesAdapter(Context context, List<Dishes> dishesList, AdminDishesViewModel adminDishesViewModel) {
        super(context, R.layout.admin_list_item_dishes, dishesList);
        this.context = context;
        this.dishesList = dishesList;
        this.adminDishesViewModel = adminDishesViewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_list_item_dishes, parent, false);
        }

        Dishes dish = dishesList.get(position);

        TextView textViewDishName = convertView.findViewById(R.id.textViewDishName);
        TextView textViewDishCost = convertView.findViewById(R.id.textViewDishCost);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);
        Button buttonEdit = convertView.findViewById(R.id.buttonEdit);

        textViewDishName.setText(dish.getName());
        textViewDishCost.setText(String.valueOf(dish.getCost() + " р."));
        buttonDelete.setOnClickListener(v -> {
            adminDishesViewModel.deleteDish(dish.getId());
        });

        buttonEdit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                AdminDishesEditFragment editFragment = new AdminDishesEditFragment();
                editFragment.setDish(dish);
                ((MainActivity) context).loadFragment(editFragment, "Изменить блюдо");
            }
        });

        return convertView;
    }

    public void updateDishesList(List<Dishes> newDishesList) {
        dishesList.clear();
        dishesList.addAll(newDishesList);
        notifyDataSetChanged();
    }

    public void removeDishById(int dishId) {
        for (int i = 0; i < dishesList.size(); i++) {
            if (dishesList.get(i).getId() == dishId) {
                dishesList.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addDishById(Dishes dish) {
        dishesList.add(dish);
        notifyDataSetChanged();
    }

    public void updateDish(Dishes dish) {
        int index = findDishIndexById(dish.getId());
        if (index != -1) {
            dishesList.set(index, dish);
            Log.d("AdminDishesAdapter", "Dish updated at index: " + index);
        } else {
            Log.d("AdminDishesAdapter", "Dish with ID: " + dish.getId() + " not found.");
        }

        notifyDataSetChanged();
    }

    private int findDishIndexById(int id) {
        for (int i = 0; i < dishesList.size(); i++) {
            if (dishesList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
