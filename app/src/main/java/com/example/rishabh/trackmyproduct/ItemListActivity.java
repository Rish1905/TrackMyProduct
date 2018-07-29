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
import android.os.Handler;
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

import com.android.volley.Request;
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
    ArrayList<Double> oldPrice = new ArrayList<Double>();
    ArrayList<String> productId = new ArrayList<String>();
    ArrayList<String> companyName = new ArrayList<String>();

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

        adapter = new CustomProductAdapter(this, R.layout.custom_single_product, productsArrayList);
        gridView.setAdapter(adapter);

        fetchDetails();

        callData();
    }

    public void fetchDetails(){

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS myProducts (url VARCHAR, productId VARCHAR, companyName VARCHAR, price VARCHAR, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT * FROM myProducts ",null);
        if(c.moveToFirst()) {
            do{
                productId.add( c.getString(c.getColumnIndex("productId")));
                companyName.add(c.getString(c.getColumnIndex("companyName")));
                oldPrice.add(Double.parseDouble(c.getString(c.getColumnIndex("price"))));
            }while(c.moveToNext());
        }
    }

    public void callData(){
        RequestQueue queue = Volley.newRequestQueue(this);
        for(int i = 0; i < productId.size(); i++){
            finalMadeURL = makeWalmartURL(productId.get(i));
            StringRequest request =  new StringRequest(Request.Method.GET,finalMadeURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.i("info",response);
                        JSONObject jsonObject = new JSONObject(response);
                        String imageURL = jsonObject.getString("thumbnailImage");
                        String itemId = ""+jsonObject.getInt("itemId");
                        Double newPrice = jsonObject.getDouble("salePrice");
                        String title = jsonObject.getString("name");
                        Drawable myIcon = getResources().getDrawable(R.drawable.folder_default);
                        Bitmap bitmap = ((BitmapDrawable) myIcon).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS myProducts (url VARCHAR, productId VARCHAR, companyName VARCHAR, price VARCHAR, date VARCHAR)");
                        Cursor c = myDatabase.rawQuery("SELECT * FROM myProducts WHERE productId='"+itemId+"'",null);
                        if(c.moveToFirst()) {
                            do{
                                Products p = new Products(itemId,data,Double.parseDouble(c.getString(c.getColumnIndex("price"))),
                                        newPrice,title,c.getString(c.getColumnIndex("companyName")));
                                productsArrayList.add(p);
                            }while(c.moveToNext());
                        }
                        //Picasso.get().load(imageURL).into();

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ItemListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(request);
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
