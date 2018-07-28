package com.example.rishabh.trackmyproduct;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rishabh.trackmyproduct.dummy.DummyContent;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    //List of Folders
    ArrayList<Products> productsArrayList;
    CustomProductAdapter adapter = null;

    //Database
    SQLiteDatabase myDatabase;

    //Variables
    String finalMadeURL = "";
    Double oldPrice = 0.0;
    String productId = "";
    String companyName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        myDatabase = this.openOrCreateDatabase("TrackMyProducts", MODE_PRIVATE, null);
        productsArrayList = new ArrayList<Products>();
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemListActivity.this,AddURLActivity.class);
                startActivity(intent);
            }
        });

        fetchDetails();

        adapter = new CustomProductAdapter(this, R.layout.custom_single_product, productsArrayList);
        gridView.setAdapter(adapter);


    }

    public void fetchDetails(){

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request =  new StringRequest(finalMadeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String imageURL = jsonObject.getString("thumbnailImage");
                    Double newPrice = jsonObject.getDouble("salePrice");
                    String title = jsonObject.getString("name");

                    //Picasso.get().load(imageURL).into();

                    //productsArrayList.add(new);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ItemListActivity.this, "Please enter the proper URL", Toast.LENGTH_SHORT).show();
            }
        });

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS myProducts (url VARCHAR, productId VARCHAR, companyName VARCHAR, price VARCHAR, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM myProducts ",null);
        if(c.moveToFirst()) {
            do{
                productId = c.getString(c.getColumnIndex("productId"));
                companyName = c.getString(c.getColumnIndex("companyName"));
                finalMadeURL = makeWalmartURL(productId);
                oldPrice = Double.parseDouble(c.getString(c.getColumnIndex("price")));
                queue.add(request);
            }while(c.moveToNext());
        }

    }

    public String makeWalmartURL(String id){
        String madeURL = "https://api.walmartlabs.com/v1/items/";
        madeURL += id + "?format=json&";
        madeURL += "apiKey=" + "3cedjptrk6df8zwubyuha6ya";
        Log.i("info", madeURL);
        return madeURL;
    }
}
