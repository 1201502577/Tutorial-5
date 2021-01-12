package com.example.recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity<RequestQueue> extends AppCompatActivity {
    /*version[] versions = {
            new version("Cupcake", "API 3", R.drawable.c),
            new version("Donut", "API 4", R.drawable.d),
            new version("Eclair", "API 5, 6, 7", R.drawable.e),
            new version("Froyo", "API 8", R.drawable.d),
            new version("Gingerbread", "API 9, 10", R.drawable.g),
            new version("Honeycomb", "API 11, 12, 13", R.drawable.h),
            new version("Ice Cream Sandwich", "API 14, 15", R.drawable.i),
            new version("Jelly Bean", "API 16, 17, 18", R.drawable.j),
            new version("KitKat", "API 19", R.drawable.images),
            new version("Lollipop", "API 21, 22", R.drawable.l),
            new version("Marshmallow", "API 23", R.drawable.m),
            new version("Nougat", "API 24, 25", R.drawable.n),
            new version("Oreo", "API 26, 27", R.drawable.o)
    };*/
    ArrayList<version> versions = new ArrayList<version>();
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter();
        //myAdapter.addElements(versions);
        recyclerView.setAdapter(myAdapter);

        Cursor cursor = getContentResolver().query(
                Uri.parse("content://mmu.edu.my.recylcer.provider"),
                new String[]{"name","description","icon"}, null, null, "name");
        if(cursor.getCount() == 0) {
// existing codes using Volley except the line for the RequestQueue
        } else {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {

                String name = cursor.getString(0);
                String description = cursor.getString(1);
                String icon = cursor.getString(2);
                version version = new version(name, description, icon);
                versions.add(version);
                ContentValues values = new ContentValues(3);
                values.put("name", name);
                values.put("description", description);
                values.put("icon", icon);
                getContentResolver().insert(Uri.parse("content://mmu.edu.my.recylce.provider"), values);
                cursor.moveToNext();

            }
            cursor.close();
            myAdapter.addElements(versions);
        }

        queue = (RequestQueue) Volley.newRequestQueue(this);
        JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, " https://raw.githubusercontent.com/kenobicjj/android/main/tutorial4.json ",
        null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        versions.clear();
                        for(int i = 0;i < response.length();i++) {
                            try {
                                JSONObject item = response.getJSONObject(i);
                                String name = item.getString("name");
                                String description = item.getString("description");
                                String icon = item.getString("icon");
                                version version =new version(name, description, icon);
                                versions.add(version);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        myAdapter.addElements(versions);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.getClass(request);
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        ArrayList<version> elements = new ArrayList<version>();
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowView = getLayoutInflater().inflate(R.layout.row, parent, false);
            return new MyViewHolder(rowView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.textView.setText(elements.get(position).getName());
            holder.textView2.setText(elements.get(position).getDescription());
            //holder.imageView.setImageResource(elements.get(position).getIcon());
            String iconUrl = "https://raw.githubusercontent.com/kenobicjj/android/main/"+elements.get(position).getIcon();
            Toast.makeText(MainActivity.this,iconUrl,Toast.LENGTH_SHORT).show();
            final LruCache<String, Bitmap> cache =new LruCache<String, Bitmap>(20);
            holder.imageView.setImageURI(iconUrl, new ImageLoader(queue, new ImageLoader.ImageCache() {
                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }
                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    }));

        }

        @Override
        public int getItemCount() {
            return elements.size();
        }
        public void addElements(ArrayList<version>versions) {
            elements.clear();
            elements.addAll(versions);
            notifyDataSetChanged();
        }
        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView textView;
            public TextView textView2;
            public ImageView imageView;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.versiontittle);
                textView2 = itemView.findViewById(R.id.versionnumber);
                imageView = itemView.findViewById(R.id.icon);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                String name = elements.get(getAdapterPosition()).getName();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        }
    }
}