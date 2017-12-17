package com.example.olaplaymp3player;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.exoplayer.PlayerActivity;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Values> abc1;
    private AppCompatActivity context;
    //to download mp3 using android's download manager
    private DownloadManager dm;
    BroadcastReceiver receiver;
    // Download Song option
    private long enqueue;

    private SqlLiteDB helper;
    ListAdapter(AppCompatActivity activity, ArrayList<Values> abc) {
        inflater = LayoutInflater.from(activity);
        abc1 = abc;
    }

    public ListAdapter(AppCompatActivity activity, int itemListImage,
                       ArrayList<Values> abc) {
        inflater = LayoutInflater.from(activity);
        abc1 = abc;
        context = activity;
    }

    @Override
    public int getCount() {
        return abc1.size();
    }

    @Override
    public Object getItem(int position) {
        return abc1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        final Values vals = (Values) this.getItem(position);
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_list_image, parent, false);

            holder.title = (TextView) view.findViewById(R.id.title);
            holder.artists =  (TextView) view.findViewById(R.id.artists);
            holder.image = (CircleImageView) view.findViewById(R.id.coverimage);
            holder.download =  (ImageView) view.findViewById(R.id.download);
            holder.play =  (ImageView) view.findViewById(R.id.play);
            holder.playlist =  (ImageView) view.findViewById(R.id.playlist);
            view.setTag(holder);
            holder.title.setText(vals.getTitle().toString());
            holder.artists.setText(vals.getArtists().toString());

        } else {
            holder = (ViewHolder) view.getTag();
            view = inflater.inflate(R.layout.item_list_image, parent, false);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.artists =  (TextView) view.findViewById(R.id.artists);
            holder.image = (CircleImageView) view.findViewById(R.id.coverimage);
            holder.download =  (ImageView) view.findViewById(R.id.download);
            holder.play =  (ImageView) view.findViewById(R.id.play);
            holder.playlist =  (ImageView) view.findViewById(R.id.playlist);
            view.setTag(holder);
            holder.title.setText(vals.getTitle().toString());
            holder.artists.setText(vals.getArtists().toString());
        }
         Glide.with(context)
                    .load(vals.getImageUrl().toString())
                    .placeholder(R.drawable.loading)
                    .crossFade()
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);

        holder.playlist.setOnClickListener(new OnClickListener() {
             @Override
            public void onClick(View v) {
            // add song details to playlist db
                 try {
                     checkPermission();
                     helper  = new SqlLiteDB(context);
                     helper.addToPlayLists(vals.getImageUrl(), vals.getTitle(), vals.getArtists(), vals.getSongURL());
                 }catch(Exception e){
                     Log.d("playlists", "exception while adding to playlists" + e.toString());
                 }finally {
                     holder.playlist.setImageDrawable(context.getResources()
                             .getDrawable(R.drawable.like_clicked));
                 }
            }
        });

        holder.download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkPermission();
                    dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    Request request = new Request(Uri.parse(vals.getSongURL()));
                    request.setTitle(vals.getTitle());
                    request.setDescription(vals.getArtists());

                    File direct = new File(Environment
                            .getExternalStorageDirectory()
                            + "/OlaPlay/Downloads");

                    if (!direct.exists()) {
                        direct.mkdirs();
                    }

                    request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, vals.getTitle().replace(" ","")+".mp3");
                    enqueue = dm.enqueue(request);
                    Toast.makeText(context,
                            "Song downloading in background..",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }finally{
                    // download video using download manager
                    try
                    {
                        receiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getAction();
                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    long downloadId = intent.getLongExtra(
                                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                                    Query query = new Query();
                                    query.setFilterById(enqueue);
                                    Cursor c = dm.query(query);
                                    if (c.moveToFirst()) {
                                        int columnIndex = c
                                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                                .getInt(columnIndex)) {
                                            Toast.makeText(context,
                                                    "Song downloaded to mobile..",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }
                            }
                        };

                    } catch (Exception e) {

                    }
                    try {
                        context.registerReceiver(receiver, new IntentFilter(
                                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    } catch (Exception e) {

                    }

                }
            }
        });

        holder.play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkPermission();
                    helper  = new SqlLiteDB(context);
                    helper.addToHistory(vals.getImageUrl(), vals.getTitle(), vals.getArtists(), vals.getSongURL());
                }catch(Exception e){
                    Log.d("HISTORY", "exception while adding to HISTORY" + e.toString());
                }
                // call player activity to play the song
                Intent i = new Intent(context, PlayerActivity.class);
                i.putExtra("url", vals.getSongURL().toString());
                // Toast.makeText(context, vals.getId().toString(), 2).show();
                context.startActivity(i);
            }
        });

        return view;
    }

    private static class ViewHolder {
        TextView title, artists;
        CircleImageView image;
        ImageView download, play, playlist;
    }



    public void clear() {
        // TODO Auto-generated method stub
        abc1.clear();
    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }
    @TargetApi(23)
    public void checkPermission(){
        haveStoragePermission();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private void showTutorial(){


    }

}
