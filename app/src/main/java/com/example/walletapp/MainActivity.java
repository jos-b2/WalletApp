package com.example.walletapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomSheetRegistro.OnTransaccionGuardadaListener {

    private DrawerLayout drawerLayout;
    private TextView tvTotalBalance;
    private LinearLayout llListaMovimientos;

    // Saldo temporal en memoria
    private double saldoActual = 1275.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvTotalBalance = findViewById(R.id.tv_total_balance);
        llListaMovimientos = findViewById(R.id.ll_lista_movimientos);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = findViewById(R.id.fab_add_transaction);
        fab.setOnClickListener(v -> {
            BottomSheetRegistro bottomSheet = new BottomSheetRegistro();
            bottomSheet.show(getSupportFragmentManager(), "BottomSheetRegistro");
        });
    }

    @Override
    public void onTransaccionGuardada(String montoStr, String descripcion) {
        try {
            double gasto = Double.parseDouble(montoStr);
            saldoActual -= gasto;

            // Actualizar Saldo Disponible
            if (tvTotalBalance != null) {
                tvTotalBalance.setText(String.format(Locale.US, "$%,.2f", saldoActual));
            }

            // Inflar y agregar una nueva tarjeta al inicio de la lista
            if (llListaMovimientos != null) {
                View nuevoItem = LayoutInflater.from(this).inflate(R.layout.item_movimiento, llListaMovimientos, false);

                TextView tvTitulo = nuevoItem.findViewById(R.id.tv_item_titulo);
                TextView tvSubtitulo = nuevoItem.findViewById(R.id.tv_item_subtitulo);
                TextView tvMonto = nuevoItem.findViewById(R.id.tv_item_monto);

                tvTitulo.setText(descripcion);
                tvSubtitulo.setText("General • Hace un momento");
                tvMonto.setText(String.format(Locale.US, "-$%.2f", gasto));

                // index 0 para que se añada arriba de los movimientos previos
                llListaMovimientos.addView(nuevoItem, 0);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato de monto inválido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Toast.makeText(this, "Ya estás en el Dashboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_transactions) {
            Toast.makeText(this, "Seleccionaste Transacciones", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_categories) {
            Toast.makeText(this, "Seleccionaste Categorías", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_goals) {
            Toast.makeText(this, "Seleccionaste Metas de Ahorro", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_reports) {
            Toast.makeText(this, "Seleccionaste Reportes", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}