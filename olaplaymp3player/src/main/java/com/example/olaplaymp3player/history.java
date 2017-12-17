package com.example.olaplaymp3player;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.R.attr.value;

public class history extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //initialize fields
    private LinearLayout mLinearScroll;
    private ListView mListView;
    private ArrayList<Values> mSongList = new ArrayList<Values>();
    private ArrayList<Values> mSongListTemp = new ArrayList<Values>();
    // Fixed row size for pagenation
    int rowSize = 5;
    private ProgressBar progress;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //url to get the songs list
    String url = "https://s3-ap-southeast-1.amazonaws.com/olaplaystudios/data.json";
    View parentLayout;
    public Values val;
    public ListAdapter adapter;
    private SqlLiteDB helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mLinearScroll = (LinearLayout) findViewById(R.id.linear_scroll);
        mListView = (ListView) findViewById(R.id.listview);
        progress = (ProgressBar) findViewById(R.id.progress);
        mSongListTemp.clear();
        mSongList.clear();
        adapter = new ListAdapter(history.this, R.layout.item_list_image, mSongListTemp);
        mListView.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        parentLayout = findViewById(android.R.id.content);
        //load songs from sqlLite db
        helper  = new SqlLiteDB(getApplicationContext());
        Cursor c = helper.getHistory();
        if (c.getCount() == 0) {
           Snackbar.make(parentLayout, "No song in your history", Snackbar.LENGTH_INDEFINITE).show();
        } else {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToNext();
                mSongListTemp.add(new Values(c.getString(0),c.getString(1),c.getString(2),c.getString(3)));
            }
            adapter.notifyDataSetChanged();
        }

    }

    private void init(ArrayList<Values> songList) {
        mSongList =songList;
        /**
         * create dynamic button according the size of array
         */
        int rem = mSongList.size() % rowSize;
        if (rem > 0) {
            for (int i = 0; i < rowSize - rem; i++) {

            }
        }
        int size = mSongList.size() / rowSize;
        addItem(0);
        for (int j = 0; j < size; j++) {
            Log.d("listview","inside pagenation");
            final int k;
            k = j;
            final Button btnPage = new Button(history.this);
            LayoutParams lp = new LinearLayout.LayoutParams(75, 75);
            lp.setMargins(5, 2, 2, 2);
            btnPage.setTextColor(Color.WHITE);
            btnPage.setTextSize(10.0f);
            btnPage.setId(j);
            btnPage.setBackground(getResources().getDrawable(R.drawable.round_button));
            btnPage.setText(String.valueOf(j + 1));
            mLinearScroll.addView(btnPage, lp);

            btnPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addItem(k);
                }
            });

        }
    }

    private void addItem(int i) {
        // TODO Auto-generated method stub
        mSongListTemp.clear();
        i = i * rowSize;
        /**
         * fill temp array list to set on page change
         */
        for (int j = 0; j < rowSize && j < mSongList.size(); j++) {
            mSongListTemp.add(mSongList.get(i));
            i = i + 1;
        }
        // set view
        setView();
    }


    //set view method
    private void setView() {
        adapter.notifyDataSetChanged();
        /**
         * On item click listener
         */
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            //open history activity
        } else if (id == R.id.nav_share) {
            //open share intent
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}

