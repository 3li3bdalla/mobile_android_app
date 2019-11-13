package com.example.problemfix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



class ProblemsListAdapter extends BaseAdapter {
    Context context;
    String list[];
    LayoutInflater inflter;

    public ProblemsListAdapter(Context applicationContext, String[] list) {
        this.context = context;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_item, null);
        TextView textView = (TextView) view.findViewById(R.id.text_view);
        textView.setText(list[i]);
        return view;
    }
}