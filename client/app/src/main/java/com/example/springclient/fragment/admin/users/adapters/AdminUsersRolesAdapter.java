package com.example.springclient.fragment.admin.users.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.springclient.R;
import com.example.springclient.fragment.admin.users.AdminUsersViewModel;
import com.example.springclient.model.Roles;
import com.example.springclient.model.Users;

import java.util.List;

public class AdminUsersRolesAdapter extends ArrayAdapter<Roles> {

    private Context context;
    private List<Roles> rolesList;
    private AdminUsersViewModel adminUsersViewModel;

    private Users user;

    public AdminUsersRolesAdapter(Context context, List<Roles> rolesList, AdminUsersViewModel adminUsersViewModel, Users user) {
        super(context, R.layout.admin_list_item_roles_user, rolesList);
        this.context = context;
        this.rolesList = rolesList;
        this.user = user;
        this.adminUsersViewModel = adminUsersViewModel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_list_item_roles_user, parent, false);
        }

        Roles roles = rolesList.get(position);
        TextView textViewUserName = convertView.findViewById(R.id.textViewRolesName);
        String rolename = roles.getName();
        textViewUserName.setText(rolename);

        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        Log.e("DeleteRole", String.valueOf(user.getId() + roles.getId()));
        buttonDelete.setOnClickListener(v -> {
            adminUsersViewModel.deleteUserRole(user.getId(), roles.getId());
            Log.e("DeleteRole", "дошёл");
            //adminUsersViewModel.loadRolesByUserId(user.getId());
        });


        return convertView;
    }

    public void updateUsersList(List<Roles> newUsersList) {
        rolesList.clear();
        rolesList.addAll(newUsersList);
        notifyDataSetChanged();
    }
    public void addUser(Roles role) {
        rolesList.add(role);
        notifyDataSetChanged();
    }


}
