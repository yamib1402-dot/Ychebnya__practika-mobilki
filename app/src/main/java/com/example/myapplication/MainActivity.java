package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BrickAdapter adapter;
    private List<Brick> brickList;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private EditText etName, etColor, etWeight, etPrice;
    private Spinner spinnerType;
    private Button btnAdd, btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем Supabase
        SupabaseClientHelper.initialize();

        initViews();
        setupSpinner();
        setupClickListeners();

        loadBricksFromSupabase();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etColor = findViewById(R.id.etColor);
        etWeight = findViewById(R.id.etWeight);
        etPrice = findViewById(R.id.etPrice);
        spinnerType = findViewById(R.id.spinnerType);
        btnAdd = findViewById(R.id.btnAdd);
        btnRefresh = findViewById(R.id.btnRefresh);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);

        recyclerView = findViewById(R.id.recyclerView);
        brickList = new ArrayList<>();
        adapter = new BrickAdapter(brickList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        String[] brickTypes = {"Керамический", "Силикатный", "Огнеупорный", "Облицовочный", "Клинкерный"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, brickTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);
    }

    private void setupClickListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBrickToSupabase();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBricksFromSupabase();
            }
        });
    }

    private void addBrickToSupabase() {
        String name = etName.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String color = etColor.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || color.isEmpty() || weightStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double price = Double.parseDouble(priceStr);

            Brick brick = new Brick(name, type, color, weight, price);

            progressBar.setVisibility(View.VISIBLE);

            SupabaseClientHelper.addBrickToDatabase(brick, this, new SupabaseClientHelper.SupabaseCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            clearForm();
                            loadBricksFromSupabase();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                            tvStatus.setText("Ошибка: " + error);
                        }
                    });
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректные числовые значения", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBricksFromSupabase() {
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("Загрузка данных...");

        SupabaseClientHelper.loadBricksFromDatabase(new SupabaseClientHelper.SupabaseBricksCallback() {
            @Override
            public void onBricksLoaded(List<Brick> bricks) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        brickList.clear();
                        brickList.addAll(bricks);
                        adapter.updateData(brickList);
                        progressBar.setVisibility(View.GONE);
                        tvStatus.setText("Загружено записей: " + brickList.size());
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        tvStatus.setText("Ошибка: " + error);
                        Toast.makeText(MainActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();

                        // Показываем тестовые данные при ошибке
                        showTestData();
                    }
                });
            }
        });
    }

    private void showTestData() {
        brickList.clear();
        brickList.add(new Brick("Красный строительный", "Керамический", "Красный", 3.5, 25.0));
        brickList.add(new Brick("Белый силикатный", "Силикатный", "Белый", 4.0, 20.0));
        brickList.add(new Brick("Огнеупорный шамотный", "Огнеупорный", "Желтый", 5.0, 45.0));
        adapter.updateData(brickList);
        tvStatus.setText("Демо данные. Загружено: " + brickList.size());
    }

    private void clearForm() {
        etName.setText("");
        etColor.setText("");
        etWeight.setText("");
        etPrice.setText("");
        spinnerType.setSelection(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBricksFromSupabase();
    }
}