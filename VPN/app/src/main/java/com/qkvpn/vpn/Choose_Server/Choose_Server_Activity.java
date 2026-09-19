package com.qkvpn.vpn.Choose_Server;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.qkvpn.vpn.R;
import com.qkvpn.vpn.api_response_model;
import com.google.gson.Gson;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Choose_Server_Activity extends AppCompatActivity {

    public RecyclerView choose_server_Recyclerview;
    ImageView toolbar_back;
    ArrayList<api_response_model> api_response_modelArrayList;
    Choose_Server_Adapter choose_server_adapter;
    SharedPreferences choose_Server_preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose__server);

        init();
    }

    private void init() {

        choose_Server_preference = getSharedPreferences("DATA", MODE_PRIVATE);
        api_response_modelArrayList = new ArrayList<>();
        choose_server_Recyclerview = findViewById(R.id.recyclerView);
        toolbar_back= findViewById(R.id.toolbar_back_button);

        if (choose_server_Recyclerview != null) {
            populate_data();
        }


        toolbar_back.setOnClickListener(v -> onBackPressed());
    }

    private void populate_data() {

        if (choose_Server_preference!=null)
        {
                api_response_modelArrayList =getAllServers_from_cache();
        }


        if (api_response_modelArrayList != null && !api_response_modelArrayList.isEmpty()) {

            for (int l = 0; l < api_response_modelArrayList.size(); l++) {
                Log.d("Type_number", "" + api_response_modelArrayList.get(l).getType());
            }

            Log.d("Response_Size" , ""+api_response_modelArrayList.size());
            choose_server_adapter = new Choose_Server_Adapter(Choose_Server_Activity.this, api_response_modelArrayList);
            choose_server_Recyclerview.setLayoutManager(new LinearLayoutManager(Choose_Server_Activity.this));
            ViewCompat.setNestedScrollingEnabled(choose_server_Recyclerview, false);
            choose_server_Recyclerview.setAdapter(choose_server_adapter);

            if (choose_server_adapter != null) {
                choose_server_adapter.notifyDataSetChanged();
                choose_server_Recyclerview.invalidate();
            }

        }
    }


    public ArrayList<api_response_model> getAllServers_from_cache() {

        try {
            Gson gson = new Gson();
            if (choose_Server_preference != null) {
                String json = choose_Server_preference.getString("saved_list_to_cache", null);
                if (json != null) {
                    if (!json.isEmpty() || !json.equals("")) {
                        return gson.fromJson(json, Model_From_Api.class);
                    } else {
                        Log.d("Empty11", "empty11");
                    }
                } else {
                    Log.d("Empty222", "empty222");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}