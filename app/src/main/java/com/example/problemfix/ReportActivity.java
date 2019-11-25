package com.example.problemfix;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.problemfix.Helper.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {


    ListView report_list;

    SharedPreferences sharedPreferences;
    ArrayList<ReportModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        report_list = findViewById(R.id.report_list);

        sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        loadDataFromBackEnd();

    }


    private void loadDataFromBackEnd() {


        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, Api.baseUrl + "order/" + sharedPreferences.getInt("id",0) + "/index",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                                list.add(new ReportModel(jsonObject.getInt("id"), jsonObject.getString("created_at"), jsonObject.getString("status")));

                            }


                            ListAdpater adpater = new ListAdpater(getApplicationContext(), list);
                            report_list.setAdapter(adpater);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Menus List", error.toString());
            }
        });

        queue.add(stringRequest);

    }


}


class ListAdpater extends BaseAdapter {


    ArrayList<ReportModel> list;
    Context context;

    ListAdpater(Context context, ArrayList<ReportModel> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater myInflater = LayoutInflater.from(context);
            convertView = myInflater.inflate(R.layout.single_report_item, parent, false);

        }


        TextView status = convertView.findViewById(R.id.status);
        TextView report_id = convertView.findViewById(R.id.report_id);
        TextView date = convertView.findViewById(R.id.date);


        status.setText(list.get(position).getStatus());
        report_id.setText(String.valueOf(list.get(position).getId()));
        date.setText(list.get(position).getCreated_at());

        return convertView;


    }
}


class ReportModel {

    private int id;
    private String created_at;
    private String status;

    public ReportModel(int id, String created_at, String status) {
        this.id = id;
        this.created_at = created_at;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getStatus() {
        return status;
    }
}