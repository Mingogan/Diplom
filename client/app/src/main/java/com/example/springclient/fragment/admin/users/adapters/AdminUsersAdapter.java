package com.example.springclient.fragment.admin.users.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.springclient.MainActivity;
import com.example.springclient.R;
import com.example.springclient.fragment.admin.users.AdminUsersEditFragment;
import com.example.springclient.fragment.admin.users.AdminUsersViewModel;
import com.example.springclient.model.Users;

import java.util.List;

public class AdminUsersAdapter extends ArrayAdapter<Users> {

    private Context context;
    private List<Users> usersList;
    private AdminUsersViewModel adminUsersViewModel;

    public AdminUsersAdapter(Context context, List<Users> usersList, AdminUsersViewModel adminUsersViewModel) {
        super(context, R.layout.admin_list_item_users, usersList);
        this.context = context;
        this.usersList = usersList;
        this.adminUsersViewModel = adminUsersViewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_list_item_users, parent, false);
        }

        Users user = usersList.get(position);
        TextView textViewUserName = convertView.findViewById(R.id.textViewUserName);
        String fullName = user.getLastName() + " " + user.getFirstName() + " " + user.getPatronymic();
        textViewUserName.setText(fullName);

        Button buttonEdit = convertView.findViewById(R.id.buttonEdit);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        buttonEdit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                AdminUsersEditFragment editFragment = new AdminUsersEditFragment();
                editFragment.setUser(user); // Pass the user object to the edit fragment
                ((MainActivity) context).loadFragment(editFragment, "Edit User");
            }
        });

        buttonDelete.setOnClickListener(v -> {
            adminUsersViewModel.deleteUser(user.getId());
        });


        return convertView;
    }

    public void updateUsersList(List<Users> newUsersList) {
        usersList.clear();
        usersList.addAll(newUsersList);
        notifyDataSetChanged();
    }

    public void removeUserById(int userId) {
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == userId) {
                usersList.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addUser(Users user) {
        usersList.add(user);
        notifyDataSetChanged();
    }

    public void updateUser(Users user) {
        int index = findUserIndexById(user.getId());
        if (index != -1) {
            usersList.set(index, user);
        }
        notifyDataSetChanged();
    }

    private int findUserIndexById(int id) {
        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}
