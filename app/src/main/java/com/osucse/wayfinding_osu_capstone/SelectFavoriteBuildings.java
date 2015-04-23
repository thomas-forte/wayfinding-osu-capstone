package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.osucse.wayfinding_api.Building;

import java.util.HashSet;
import java.util.Set;

public class SelectFavoriteBuildings extends ActionBarActivity {

    public BuildingListAdapter adapter;
    public ListView listView;
    public Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_favorite_buildings);

        listView = (ListView) findViewById(R.id.select_favorite_buildings_list);
        saveButton = (Button) findViewById(R.id.select_favorite_buildings_save_button);

        adapter = new BuildingListAdapter(getApplicationContext(), BuildingListAdapter.cloneBuildingList());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building itemClicked = adapter.getItem(position);

                itemClicked.isfavorite = !itemClicked.isfavorite;

                SelectFavoriteBuildings.this.adapter.getFilter().filter("");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> saveItems = new HashSet<String>();

                for (int i = 0;  i < adapter.getCount(); i++) {
                    if (adapter.getItem(i).isfavorite){
                        saveItems.add(adapter.getItem(i).getName());
                    }
                }

                Settings.setFavoritesToSettings(saveItems);

                SelectFavoriteBuildings.this.finish();
            }
        });
    }
}
