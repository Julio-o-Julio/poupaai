package com.example.poupaai.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.poupaai.R;
import com.example.poupaai.databinding.ActivityMainBinding;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.User;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements FragmentMyExpenses.OnMonthSelectedListener {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private User loggedUser;
    private Month selectedMonth;

    @Override
    public void onMonthSelected(Month month) {
        this.selectedMonth = month;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            updateFabAction(destination.getId());
            invalidateOptionsMenu();
        });

        loggedUser = getIntent().getParcelableExtra("user");

        Log.d("MainActivity", "LoggedUser ID: " + loggedUser.getUid());

        if (loggedUser != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", loggedUser);
            Log.d("MainActivity", "Passing loggedUser: " + loggedUser);
            navController.navigate(R.id.action_to_fragment_my_expenses_monthly, bundle);
        }

        binding.fab.setOnClickListener(view -> {
            int destinationId = navController.getCurrentDestination().getId();
            handleFabClick(destinationId, view);
        });
    }

    private void updateFabAction(int destinationId) {
        if (destinationId == R.id.FragmentMyExpensesMonthly) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setContentDescription("Adicionar Despesa");
        } else if (destinationId == R.id.FragmentMyExpenses) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setContentDescription("Adicionar Despesa");
        } else {
            binding.fab.setVisibility(View.GONE);
        }
    }

    private void handleFabClick(int destinationId, View view) {
        if (destinationId == R.id.FragmentMyExpensesMonthly) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_add_expenses, bundle);
            Snackbar.make(view, R.string.add_expenses, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show();
        } else if (destinationId == R.id.FragmentMyExpenses) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", loggedUser);
            if (selectedMonth != null) {
                bundle.putParcelable("month", selectedMonth);
            }

            navController.navigate(R.id.action_to_fragment_add_expenses, bundle);
            Snackbar.make(view, R.string.add_expenses, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        if (currentFragment instanceof FragmentMyExpensesMonthly) {
            menu.findItem(R.id.my_expenses_monthly).setVisible(false);
            menu.findItem(R.id.my_profile).setVisible(true);
        } else if (currentFragment instanceof FragmentMyProfile) {
            menu.findItem(R.id.my_expenses_monthly).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Bundle bundle = new Bundle();

        if (id == R.id.my_expenses_monthly) {
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_my_expenses_monthly, bundle);
            return true;
        } else if (id == R.id.my_profile) {
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_my_profile, bundle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
