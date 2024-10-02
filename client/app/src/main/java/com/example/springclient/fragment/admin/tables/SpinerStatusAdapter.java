package com.example.springclient.fragment.admin.tables;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.springclient.model.TablesStatus;

import java.util.List;

public class SpinerStatusAdapter extends ArrayAdapter<TablesStatus> {

    public SpinerStatusAdapter(Context context, int resource, List<TablesStatus> statuses) {
        super(context, resource, statuses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        TablesStatus status = getItem(position);
        textView.setText(status.getStatusName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), android.R.layout.simple_spinner_dropdown_item, null);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        TablesStatus status = getItem(position);
        textView.setText(status.getStatusName());
        return convertView;
    }
}
