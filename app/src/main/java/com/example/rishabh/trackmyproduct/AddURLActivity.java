package com.example.rishabh.trackmyproduct;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddURLActivity extends AppCompatActivity {

    EditText urlText;
    Button addURL;
    Button walmart;

    //Company Name
    String company = "";
    String finalURL = "";
    String dateAndTime = "";
    String productID = "";
    String price = "";

    //Database
    SQLiteDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_url);

        init();
        addURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateURL()) return;
                finalURL = urlText.getText().toString().trim();
                if (company.equals("walmart")) InsertWalmartIntoDatabase();
            }
        });

        walmart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddURLActivity.this, WebViewActivity.class);
                intent.putExtra("url", "http://www.walmart.com");
                startActivity(intent);
            }
        });
    }

    public void init() {
        urlText = (EditText) findViewById(R.id.url);
        addURL = (Button) findViewById(R.id.button);
        walmart = (Button) findViewById(R.id.walmart);
        urlText.setText("https://www.walmart.com/ip/Char-Broil-3-or-4-Burner-All-Season-Grill-Cover/49316795");
        myDatabase = this.openOrCreateDatabase("TrackMyProducts", MODE_PRIVATE, null);
    }

    public boolean validateURL() {
        String url = urlText.getText().toString().trim();
        if (url.isEmpty()) {
            Toast.makeText(this, "URL can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www")) {
            if (url.contains("www.walmart.com")) {
                if (url.contains("/ip/")) {
                    company = "walmart";
                    return true;
                } else {
                    Toast.makeText(this, "Enter the particular product link", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter the valid Company website", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter the valid URL", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void InsertWalmartIntoDatabase() {

        String modifiedURL = finalURL;
        modifiedURL = modifiedURL.replace("https://", "");
        modifiedURL = modifiedURL.replace("http://", "");
        modifiedURL = modifiedURL.replace("www.walmart.com/ip/", "");
        String[] temp = modifiedURL.split("/");
        char[] temp1 = temp[1].toCharArray();
        for (int i = 0; i < temp1.length; i++) {
            if (temp1[i] < 48 || temp1[i] > 58) break;
            productID += temp1[i];
        }

        if (!CheckDuplicate(productID)) return;

        String madeURL = "https://api.walmartlabs.com/v1/items/";
        madeURL += productID + "?format=json&";
        madeURL += "apiKey=" + "3cedjptrk6df8zwubyuha6ya";
        Log.i("info", madeURL);

        StringRequest request =  new StringRequest(madeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Code",response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dateAndTime = sdf.format(new Date());

            myDatabase.execSQL("CREATE TABLE IF NOT EXISTS myProducts (url VARCHAR, productId VARCHAR, companyName VARCHAR, price VARCHAR, date VARCHAR)");
            String sql = "INSERT INTO myProducts VALUES (?,?,?,?,?)";
            SQLiteStatement statement = myDatabase.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1,finalURL);
            statement.bindString(2,productID);
            statement.bindString(3,company);
            statement.bindString(4,""+jsonObject.getDouble("salePrice"));
            statement.bindString(5,dateAndTime);
            statement.executeInsert();

            Toast.makeText(getApplicationContext(), "Product Added", Toast.LENGTH_SHORT).show();
            finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddURLActivity.this, "Please enter the proper URL", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public boolean CheckDuplicate(String ProductID) {
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS myProducts (url VARCHAR, productId VARCHAR, companyName VARCHAR, price VARCHAR, date VARCHAR)");
        Cursor c = myDatabase.rawQuery("SELECT productId,companyName FROM myProducts ", null);
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex("productId"));
                String cName = c.getString(c.getColumnIndex("companyName"));
                if (ProductID.equals(name) && cName.equals(company)) {
                    Toast.makeText(this, "Product ID Already exsist. Try Again!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } while (c.moveToNext());
        }
        return true;
    }
}
