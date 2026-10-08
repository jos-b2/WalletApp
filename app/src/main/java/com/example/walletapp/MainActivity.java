package com.example.walletapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomSheetRegistro.OnTransaccionGuardadaListener {

    private DrawerLayout drawerLayout;
    private TextView tvTotalBalance;
    private LinearLayout llListaMovimientos;

    // Componentes del Dashboard Dinámico
    private TextView tvMonthlyIncomes;
    private TextView tvMonthlyExpenses;
    private TextView tvBudgetPercentage;
    private TextView tvBudgetSpent;
    private TextView tvBudgetLimit;
    private ProgressBar pbPresupuesto;

    // Variables de control financiero
    private double saldoActual = 1275.00;
    private double totalIngresosMes = 1850.00;
    private double totalGastosMes = 575.00;
    private double limitePresupuesto = 1850.00; // Puedes ajustarlo según las metas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // CONFIGURACIÓN PARA MOSTRAR LOGO Y TÍTULO JUNTOS:
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            android.widget.LinearLayout titleLayout = new android.widget.LinearLayout(this);
            titleLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            titleLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);

            android.widget.ImageView logoView = new android.widget.ImageView(this);
            logoView.setImageResource(R.drawable.logo1);

            int sizeInPx = (int) (32 * getResources().getDisplayMetrics().density);
            android.widget.LinearLayout.LayoutParams imageParams = new android.widget.LinearLayout.LayoutParams(sizeInPx, sizeInPx);
            imageParams.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
            logoView.setLayoutParams(imageParams);
            logoView.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);
            titleLayout.addView(logoView);

            android.widget.TextView textView = new android.widget.TextView(this);
            textView.setText("Wallet App");
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleLayout.addView(textView);

            androidx.appcompat.app.ActionBar.LayoutParams params = new androidx.appcompat.app.ActionBar.LayoutParams(
                    androidx.appcompat.app.ActionBar.LayoutParams.WRAP_CONTENT,
                    androidx.appcompat.app.ActionBar.LayoutParams.MATCH_PARENT
            );
            getSupportActionBar().setCustomView(titleLayout, params);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Vistas principales
        tvTotalBalance = findViewById(R.id.tv_total_balance);
        llListaMovimientos = findViewById(R.id.ll_lista_movimientos);

        // Vistas del Monitor de Presupuesto y Resumen
        tvMonthlyIncomes = findViewById(R.id.tv_monthly_incomes);
        tvMonthlyExpenses = findViewById(R.id.tv_monthly_expenses);
        tvBudgetPercentage = findViewById(R.id.tv_budget_percentage);
        tvBudgetSpent = findViewById(R.id.tv_budget_spent);
        tvBudgetLimit = findViewById(R.id.tv_budget_limit);
        pbPresupuesto = findViewById(R.id.pb_presupuesto);

        // Actualizamos vista inicial
        actualizarTablero();

        // Enlace "Ver todos"
        TextView tvVerTodos = findViewById(R.id.tv_ver_todos);
        if (tvVerTodos != null) {
            tvVerTodos.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TransactionsActivity.class)));
        }

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
    public void onTransaccionGuardada(String montoStr, String descripcion, boolean esIngreso) {
        try {
            double valor = Double.parseDouble(montoStr);

            if (esIngreso) {
                saldoActual += valor;
                totalIngresosMes += valor;
            } else {
                saldoActual -= valor;
                totalGastosMes += valor;
            }

            // Recalcula Saldo, Ingresos, Gastos y la Barra de Presupuesto
            actualizarTablero();

            // Inserta el nuevo item arriba en la lista
            if (llListaMovimientos != null) {
                View nuevoItem = LayoutInflater.from(this).inflate(R.layout.item_movimiento, llListaMovimientos, false);

                TextView tvTitulo = nuevoItem.findViewById(R.id.tv_item_titulo);
                TextView tvSubtitulo = nuevoItem.findViewById(R.id.tv_item_subtitulo);
                TextView tvMonto = nuevoItem.findViewById(R.id.tv_item_monto);

                tvTitulo.setText(descripcion);
                tvSubtitulo.setText((esIngreso ? "Ingreso" : "Gasto") + " • Hace un momento");

                if (esIngreso) {
                    tvMonto.setText(String.format(Locale.US, "+$%.2f", valor));
                    try {
                        tvMonto.setTextColor(ContextCompat.getColor(this, R.color.primary_navy));
                    } catch (Exception e) {
                        tvMonto.setTextColor(Color.parseColor("#050A30"));
                    }
                } else {
                    tvMonto.setText(String.format(Locale.US, "-$%.2f", valor));
                    try {
                        tvMonto.setTextColor(ContextCompat.getColor(this, R.color.expense_gold));
                    } catch (Exception e) {
                        tvMonto.setTextColor(Color.parseColor("#B79347"));
                    }
                }

                llListaMovimientos.addView(nuevoItem, 0);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Formato de monto inválido", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Recalcula y refresca todos los indicadores financieros de la pantalla
     */
    private void actualizarTablero() {
        if (tvTotalBalance != null) {
            tvTotalBalance.setText(String.format(Locale.US, "$%,.2f", saldoActual));
        }

        if (tvMonthlyIncomes != null) {
            tvMonthlyIncomes.setText(String.format(Locale.US, "+$%,.2f", totalIngresosMes));
        }

        if (tvMonthlyExpenses != null) {
            tvMonthlyExpenses.setText(String.format(Locale.US, "-$%,.2f", totalGastosMes));
        }

        if (tvBudgetSpent != null) {
            tvBudgetSpent.setText(String.format(Locale.US, "Gastado: $%,.2f", totalGastosMes));
        }

        if (tvBudgetLimit != null) {
            tvBudgetLimit.setText(String.format(Locale.US, "Límite: $%,.2f", limitePresupuesto));
        }

        // Cálculo dinámico del porcentaje de presupuesto consumido
        if (limitePresupuesto > 0) {
            int porcentaje = (int) Math.round((totalGastosMes / limitePresupuesto) * 100);
            if (porcentaje > 100) porcentaje = 100; // Limitar al 100% en la barra visual

            if (pbPresupuesto != null) {
                pbPresupuesto.setProgress(porcentaje);
            }

            if (tvBudgetPercentage != null) {
                int porcentajeReal = (int) Math.round((totalGastosMes / limitePresupuesto) * 100);
                tvBudgetPercentage.setText(porcentajeReal + "% consumido");
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Toast.makeText(this, "Ya estás en el Dashboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_transactions) {
            startActivity(new Intent(this, TransactionsActivity.class));
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(MainActivity.this, gestion_categoria.class));
        } else if (id == R.id.nav_goals) {
            startActivity(new Intent(MainActivity.this, meta_ahorro.class));
        } else if (id == R.id.nav_reports) {
            startActivity(new Intent(this, ReportsActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}