package com.example.poupaai.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.poupaai.R;
import com.example.poupaai.databinding.ActivityMainBinding;
import com.example.poupaai.entities.User;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private User loggedUser;

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

        binding.fab.setOnClickListener(view -> {
            int destinationId = navController.getCurrentDestination().getId();
            handleFabClick(destinationId, view);
        });
    }

    private void updateFabAction(int destinationId) {
        if (destinationId == R.id.FragmentMyExpensesMonthly) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setContentDescription("Adicionar Despesa");
        }/* else if (destinationId == R.id.FragmentAddresses) {
            binding.fab.setVisibility(View.VISIBLE);
            binding.fab.setContentDescription("Adicionar Endereço");
        }*/ else {
            binding.fab.setVisibility(View.GONE);
        }
    }

    private void handleFabClick(int destinationId, View view) {
        /*if (destinationId == R.id.FragmentMyExpensesMonthly) {
            navController.navigate(R.id.action_to_add_city);
            Snackbar.make(view, R.string.add_city, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show();
        } else if (destinationId == R.id.FragmentAddresses) {
            navController.navigate(R.id.action_to_add_address);
            Snackbar.make(view, R.string.add_address, Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show();
        }*/
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
        }/* else if (currentFragment instanceof FragmentAddresses) {
            menu.findItem(R.id.all_cities).setVisible(true);
            menu.findItem(R.id.all_address).setVisible(false);
            menu.findItem(R.id.all_profiles).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(true);
        } else if (currentFragment instanceof FragmentProfiles) {
            menu.findItem(R.id.all_cities).setVisible(true);
            menu.findItem(R.id.all_address).setVisible(true);
            menu.findItem(R.id.all_profiles).setVisible(false);
            menu.findItem(R.id.my_profile).setVisible(true);
        }*/ else if (currentFragment instanceof FragmentMyProfile) {
            menu.findItem(R.id.my_expenses_monthly).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.my_expenses_monthly) {
            navController.navigate(R.id.action_to_fragment_my_expenses_monthly);
            return true;
        }/* else if (id == R.id.all_address) {
            navController.navigate(R.id.action_to_all_addresses);
            return true;
        } else if (id == R.id.all_profiles) {
            navController.navigate(R.id.action_to_all_profiles);
            return true;
        }*/ else if (id == R.id.my_profile) {
            Bundle bundle = new Bundle();
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
