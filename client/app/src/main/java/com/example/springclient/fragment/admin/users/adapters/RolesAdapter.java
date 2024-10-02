package com.example.springclient.fragment.admin.users.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.springclient.model.Roles;

import java.util.List;

public class RolesAdapter extends ArrayAdapter<Roles> {

    public RolesAdapter(Context context, List<Roles> roles) {
        super(context, android.R.layout.simple_spinner_item, roles);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName());
        return view;
    }
}