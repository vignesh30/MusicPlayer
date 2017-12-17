package com.example.olaplaymp3player;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class MainActivity extends AppCompatActivity
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
    SearchView searchView;

    //to store first time launch in shared preference
    private SharedPreferences pref;
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        parentLayout = findViewById(android.R.id.content);
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        removePageButtons();
                        try {
                            //get the refresh the songs list on swipe down
                            GetSongs get = new GetSongs();
                            get.execute(url);
                        } catch (Exception e) {
                            Snackbar.make(parentLayout,"Error occured please try again later..", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });

        mSongListTemp.clear();
        adapter = new ListAdapter(MainActivity.this, R.layout.item_list_image, mSongListTemp);
        mListView.setAdapter(adapter);
        ConnectionDetector checkConnection = new ConnectionDetector(MainActivity.this);
        //check for internet and load the songs
        if(checkConnection.isConnectingToInternet()){
            GetSongs get = new GetSongs();
            get.execute(url);
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            progress.setVisibility(View.INVISIBLE);
            Snackbar.make(parentLayout,"Please check your internet connection..", Snackbar.LENGTH_SHORT).show();

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
            final Button btnPage = new Button(MainActivity.this);
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
    }

    private void removePageButtons(){
        if(mSongList.size() >  0){
            mLinearScroll.removeAllViews();
        }
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
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main, menu);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchViewMenuItem.getActionView();

        searchView.setQueryHint("Search by name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
            Toast.makeText(getApplicationContext(),"searched for "+ query, Toast.LENGTH_SHORT).show();
                ArrayList<Values> filteredList = new ArrayList<Values>();
                filteredList = getFiteredSongList(query.toLowerCase());
                if(filteredList.size() > 0) {
                    init(filteredList);
                }else{
                    Toast.makeText(getApplicationContext(),"No results found..", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                removePageButtons();
                GetSongs get = new GetSongs();
                get.execute(url);
                return false;
            }
        });
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            //open history activity
            Intent i = new Intent(MainActivity.this, history.class);
            MainActivity.this.startActivity(i);
        } else if (id == R.id.playLists) {
            //open playlists activity
            Intent i = new Intent(MainActivity.this, PlayList.class);
            MainActivity.this.startActivity(i);
        }else if (id == R.id.nav_share) {
            //open share intent
            String shareBody = "Listen to super hit songs in Ola Play Studios - https://play.google.com/store/apps/details?id=com.olaplay.music";
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "Ola Play Studios - Android App");
            startActivity(Intent
                    .createChooser(sharingIntent, "Share using"));
        }else if (id == R.id.portfolio) {
            //open My work activuty
            Intent i = new Intent(MainActivity.this, MyWork.class);
            MainActivity.this.startActivity(i);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class GetSongs extends AsyncTask<String , Void ,String> {
        String server_response;
        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("GetSongs", server_response);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Response", "" + server_response);
            mSwipeRefreshLayout.setRefreshing(false);
            progress.setVisibility(View.INVISIBLE);
            try {

                Log.d("res values", "server_response: "+ server_response);
                JSONArray jarray = new JSONArray(server_response);

                ArrayList<Values> res = new ArrayList<Values>();
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject data = jarray.getJSONObject(i);
                    res.add(new Values(data.getString("cover_image"), data.getString("song"), data.getString("artists"), data.getString("url")));
                }
                init(res);
            }catch(Exception e){
                Log.d("Error parsing ", "parsing : "+ e.toString());
                Snackbar.make(parentLayout,"unexpected error occured..please try again later..", Snackbar.LENGTH_SHORT).show();

            }
      }
    }

    //get searched song by name
    private ArrayList<Values> getFiteredSongList(String name){
        ArrayList<Values> res = new ArrayList<Values>();
        int i =0;
        res.clear();
        for (int j = 0; j < mSongList.size(); j++) {
            if(mSongList.get(i).getTitle().toLowerCase().contains(name)) {
                res.add(mSongList.get(i));
                Log.d("filter", "getFiteredSongList: match found");
            }
            i = i + 1;
        }
        return res;
    }

    // Converting InputStream to String
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}

