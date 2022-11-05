package com.example.apiwork;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class AddApi extends AppCompatActivity {

    // creating variables for our edittext,
    // button, textview and progressbar.
    private EditText AddUser, AddKonfirurate, AddZena;
    private Button AddButton;
    private TextView responseTV;
    private ProgressBar loadingPB;
    private ImageView imageButton;
    String Img="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_api);
        configureBackButton();
        AddUser = findViewById(R.id.userAdd);
        AddKonfirurate = findViewById(R.id.konfigurateAdd);
        AddZena=findViewById(R.id.zenaAdd);
        responseTV = findViewById(R.id.idTVResponse);
        AddButton = findViewById(R.id.addButton);
        loadingPB = findViewById(R.id.idLoadingPB);
        imageButton=findViewById(R.id.imageViewAdd);

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AddUser.getText().toString().isEmpty() && AddKonfirurate.getText().toString().isEmpty() && AddZena.getText().toString().isEmpty()) {
                    Toast.makeText(AddApi.this, "Please enter both the values", Toast.LENGTH_SHORT).show();
                    return;
                }
                postData(AddUser.getText().toString(), AddKonfirurate.getText().toString(), Integer.parseInt(AddZena.getText().toString()), Img.toString());
            }
        });
    }
    public void onClickChooseImage(View view)
    {
        getImage();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!= null && data.getData()!= null)
        {
            if(resultCode==RESULT_OK)
            {
                Log.d("MyLog","Image URI : "+data.getData());
                imageButton.setImageURI(data.getData());
                Bitmap bitmap = ((BitmapDrawable)imageButton.getDrawable()).getBitmap();
                encodeImage(bitmap);

            }
        }
    }

    private void getImage()
    {
        Intent intentChooser= new Intent();
        intentChooser.setType("image/*");
        intentChooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentChooser,1);
    }

    private String encodeImage(Bitmap bitmap) {
        int prevW = 150;
        int prevH = bitmap.getHeight() * prevW / bitmap.getWidth();
        Bitmap b = Bitmap.createScaledBitmap(bitmap, prevW, prevH, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Img= Base64.getEncoder().encodeToString(bytes);
            return Img;
        }
        return "";
    }
    private void configureBackButton() {
        Button back = (Button) findViewById(R.id.otmAdd);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void postData(String user, String konfiguracia, int zena, String IMG) {
        loadingPB.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ngknn.ru:5101/NGKNN/СорокинДА/api/")
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();
        // below line is to create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // passing data from our text fields to our modal class.
        DataModal modal = new DataModal(user,konfiguracia,zena,IMG);

        // calling a method to create a post and passing our modal class.
        Call<DataModal> call = retrofitAPI.createPost(modal);

        // on below line we are executing our method.
           call.enqueue(new Callback<DataModal>() {
               @Override
               public void onResponse(Call<DataModal> call, Response<DataModal> response) {
                   // this method is called when we get response from our api.
                   Toast.makeText(AddApi.this, "Data added to API", Toast.LENGTH_SHORT).show();
                   loadingPB.setVisibility(View.GONE);
                   AddUser.setText("");
                   AddKonfirurate.setText("");
                   AddZena.setText("");
                   // we are getting response from our body
                   // and passing it to our modal class.
                   DataModal responseFromAPI = response.body();

               // on below line we are getting our data from modal class and adding it to our string.
                String responseString = "Response Code : " + response.code() + "\nUser : " + responseFromAPI.getUser() + "\n" + "Konfiguracia : " + responseFromAPI.getKonfiguracia()+ "\n" + "Zena : " + responseFromAPI.getZena() + "\n" + "Img : " + responseFromAPI.getImg();
                responseTV.setText(responseString);
            }
            @Override
            public void onFailure(Call<DataModal> call, Throwable t) {
                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
               }

           }