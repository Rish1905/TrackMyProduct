package com.example.rishabh.trackmyproduct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddURLActivity extends AppCompatActivity {

    EditText urlText;
    Button addURL;
    Button walmart;

    //Company Name
    String company = "";
    String finalURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_url);

        init();
        if(!validateURL()) return;
        finalURL = urlText.getText().toString().trim();
        if(company.equals("walmart")) InsertWalmartIntoDatabase();

        walmart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddURLActivity.this,WebViewActivity.class);
                intent.putExtra("url","http://www.walmart.com");
                startActivity(intent);
            }
        });
    }

    public void init(){
        urlText = (EditText) findViewById(R.id.url);
        addURL = (Button) findViewById(R.id.button);
        walmart = (Button) findViewById(R.id.walmart);
    }

    public boolean validateURL(){
        String url = urlText.getText().toString().trim();
        if(url.isEmpty()){
            return false;
        }
        if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www")){
            if(url.contains("www.walmart.com")){
                if(url.contains("/ip/")){
                    company = "walmart";
                    return true;
                }
                else{
                    Toast.makeText(this, "Enter the particular product link", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Enter the valid Company website", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Enter the valid URL", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void InsertWalmartIntoDatabase(){



    }
}
