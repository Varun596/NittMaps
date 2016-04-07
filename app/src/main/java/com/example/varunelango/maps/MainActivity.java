package com.example.varunelango.maps;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    public Bundle bundle = new Bundle();
    public static final String  URL = "https://spider.nitt.edu/lateral/appdev/coordinates?category=";
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String[] Names = new String[]{"Hostels", "Departments", "Messes", "Canteens"};
        final int Image[] = new int[]{R.mipmap.h,R.mipmap.d,R.mipmap.m,R.mipmap.c};


        List<HashMap<String,String>> aList = new ArrayList<HashMap<String, String>>();

        for(int i=0; i<4; i++){
            HashMap<String,String> hm = new HashMap<String,String>();
            hm.put("Name", Names[i]);
            hm.put("Image", Integer.toString(Image[i]));
            aList.add(hm);
        }

        String[] from = {"Image","Name" };
        int[] to = { R.id.ContactImage, R.id.ContactText };
        SimpleAdapter nameAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.custom_row, from, to);
        ListView CLV = (ListView) findViewById(R.id.ContactsListView);
        CLV.setAdapter(nameAdapter);

        CLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (l == 0) {
                    data="Hostels";
                }
                if(l==1)
                {
                    data="Departments";
                }
                if(l==2)
                {
                    data="Messes";
                }
                if(l==3)
                {
                    data="Canteens";
                }
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                bundle.putString("Data",data);
                intent.putExtra("Bundle",bundle);
                startActivity(intent);


            }
        });



    }




}
