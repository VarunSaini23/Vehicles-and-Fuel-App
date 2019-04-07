package com.example.android.searchvehicle;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchCitiesDialogActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    FragmentManager manager;
    public String cityName;
    public String cityLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cities_dialog);
        this.setFinishOnTouchOutside(true);
        setTitle("Select city");

        final ArrayList<String> arrayList = getIntent().getStringArrayListExtra("arrayList");
        final ArrayList<String> arrayList1 = getIntent().getStringArrayListExtra("arrayList1");

        adapter = new ArrayAdapter<String>(this, R.layout.list_item_fuel_states, R.id.product_name, arrayList);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        EditText edit = findViewById(R.id.edit);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchCitiesDialogActivity.this.adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

}
