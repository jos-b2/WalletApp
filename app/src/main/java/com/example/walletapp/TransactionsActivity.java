package com.example.walletapp;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private TransactionsAdapter adapter;
    private List<TransactionItem> allTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        Toolbar toolbar = findViewById(R.id.toolbar_transactions);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allTransactions = new ArrayList<>();
        allTransactions.add(new TransactionItem("Supermercado La Despensa", "Alimentación • Hoy, 03:45 PM", "-$45.00", "Completado"));
        allTransactions.add(new TransactionItem("Pago de Salario Mensual", "Ingresos • Ayer, 09:00 AM", "+$1,500.00", "Completado"));
        allTransactions.add(new TransactionItem("Gasolinera Puma", "Transporte • 06 Oct, 04:15 PM", "-$25.00", "Completado"));
        allTransactions.add(new TransactionItem("Factura de Energía Eléctrica", "Servicios • 05 Oct, 10:30 AM", "-$35.50", "Completado"));
        allTransactions.add(new TransactionItem("Cafetería Local", "Alimentación • 04 Oct, 02:20 PM", "-$6.75", "Completado"));

        adapter = new TransactionsAdapter(allTransactions);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.search_view_transactions);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<TransactionItem> filteredList = new ArrayList<>();
        for (TransactionItem item : allTransactions) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    item.getSubtitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}