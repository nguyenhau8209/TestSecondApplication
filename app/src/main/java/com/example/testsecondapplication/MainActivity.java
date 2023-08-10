package com.example.testsecondapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testsecondapplication.adapter.MotosAdapter;
import com.example.testsecondapplication.api.MotosApi;
import com.example.testsecondapplication.api.MotosApiClient;
import com.example.testsecondapplication.modal.Motos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabCreate;
    private MotosAdapter adapter;

    private MotosApi motosApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MotosAdapter();
        recyclerView.setAdapter(adapter);
        fabCreate = findViewById(R.id.floatingActionButton2);
        motosApi = MotosApiClient.getRetrofitInstance().create(MotosApi.class);
        getAllMotos();

        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMotoDialog();
            }
        });
    }

    private void getAllMotos() {
        MotosApiClient motosApiClient = new MotosApiClient();
        motosApiClient.getMotosClient(new Callback<List<Motos>>() {
            @Override
            public void onResponse(Call<List<Motos>> call, Response<List<Motos>> response) {
                if (response.isSuccessful()) {

                    List<Motos> list = response.body();
                    List<Motos> sortedItemList = list.stream()
                            .sorted((item1, item2) -> {
                                String createdAt1 = item1.getCreateAt();
                                String createdAt2 = item2.getCreateAt();

                                if (createdAt1 == null || createdAt2 == null) {
                                    return 0; // Xử lý trường hợp chuỗi createdAt là null
                                }

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                                try {
                                    Date date1 = format.parse(createdAt1);
                                    Date date2 = format.parse(createdAt2);
                                    return date2.compareTo(date1); // Sắp xếp giảm dần (mới nhất trước)
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return 0;
                            })
                            .collect(Collectors.toList());
                    adapter.setMotos(sortedItemList);
                }
            }

            @Override
            public void onFailure(Call<List<Motos>> call, Throwable t) {

            }
        });
    }

    private void showAddMotoDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Create Moto");

        // Find and initialize dialog views here
        // For example, EditText to input item name
        EditText etNameCreate = dialogView.findViewById(R.id.etNameCreate);
        EditText etPriceCreate = dialogView.findViewById(R.id.etPriceCreate);
        EditText etColorCreate = dialogView.findViewById(R.id.etColorCreate);


        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle adding the new item here
                // For example, get the input from EditText and make API call
                // Tạo một item mới và gửi yêu cầu để thêm vào API
                Motos moto = new Motos();
                moto.setName(etNameCreate.getText().toString());
                moto.setPrice(Double.parseDouble(etPriceCreate.getText().toString()));
                moto.setColor(etColorCreate.getText().toString());
                Call<Motos> createCall = motosApi.createMoto(moto);
                createCall.enqueue(new Callback<Motos>() {
                    @Override
                    public void onResponse(Call<Motos> call, Response<Motos> response) {
                        Motos createdItem = response.body();
                        Log.d("Create Moto", "onResponse: " + createdItem);
                        getAllMotos();
                        Toast.makeText(MainActivity.this, "Create successfully!", Toast.LENGTH_SHORT).show();

                        // Xử lý item đã được thêm ở đây
                    }

                    @Override
                    public void onFailure(Call<Motos> call, Throwable t) {
                        // Xử lý lỗi ở đây
                        Toast.makeText(MainActivity.this, "Create false!", Toast.LENGTH_SHORT).show();

                    }
                });

                etNameCreate.setText("");
                etPriceCreate.setText("");
                etColorCreate.setText("");
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}