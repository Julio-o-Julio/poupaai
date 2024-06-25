package com.example.poupaai.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.ActivityMainBinding;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.Notification;
import com.example.poupaai.entities.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity implements FragmentMyExpenses.OnMonthSelectedListener {
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private LocalDatabase db;
    private User loggedUser;
    private Month selectedMonth;
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.POST_NOTIFICATIONS};

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

        db = LocalDatabase.getDatabase(this);

        loggedUser = getIntent().getParcelableExtra("user");

        if (loggedUser != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", loggedUser);
            navController.navigate(R.id.action_to_fragment_my_expenses_monthly, bundle);
        }

        createNotificationChannel();

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        } else {
            checkAndSendNotifications();
        }

        binding.fab.setOnClickListener(view -> {
            int destinationId = navController.getCurrentDestination().getId();
            handleFabClick(destinationId, view);
        });
    }

    private void checkAndSendNotifications() {
        List<Notification> notifications = db.notification().getUnreadNotifications(loggedUser.getUid());

        for (Notification notification : notifications) {
            sendNotification(notification);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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
            menu.findItem(R.id.add_friends).setVisible(true);
            menu.findItem(R.id.my_notifications).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(true);
            menu.findItem(R.id.friends).setVisible(true);
        } else if (currentFragment instanceof FragmentMyProfile) {
            menu.findItem(R.id.my_expenses_monthly).setVisible(true);
            menu.findItem(R.id.add_friends).setVisible(true);
            menu.findItem(R.id.my_notifications).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(false);
            menu.findItem(R.id.friends).setVisible(true);
        } else if (currentFragment instanceof FragmentFriends) {
            menu.findItem(R.id.friends).setVisible(false);
            menu.findItem(R.id.my_expenses_monthly).setVisible(true);
            menu.findItem(R.id.add_friends).setVisible(true);
            menu.findItem(R.id.my_notifications).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(true);
        } else if (currentFragment instanceof FragmentMyNotifications) {
            menu.findItem(R.id.my_expenses_monthly).setVisible(true);
            menu.findItem(R.id.add_friends).setVisible(true);
            menu.findItem(R.id.my_notifications).setVisible(false);
            menu.findItem(R.id.my_profile).setVisible(true);
            menu.findItem(R.id.friends).setVisible(true);
        } else if (currentFragment instanceof FragmentAddFriends) {
            menu.findItem(R.id.my_expenses_monthly).setVisible(true);
            menu.findItem(R.id.add_friends).setVisible(false);
            menu.findItem(R.id.my_notifications).setVisible(true);
            menu.findItem(R.id.my_profile).setVisible(true);
            menu.findItem(R.id.friends).setVisible(true);
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
        } else if (id == R.id.add_friends) {
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_add_friends, bundle);
            return true;
        } else if (id == R.id.friends) {
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_friends, bundle);
            return true;
        } else if (id == R.id.my_notifications) {
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_my_notifications, bundle);
            return true;
        } else if (id == R.id.my_profile) {
            bundle.putParcelable("user", loggedUser);

            navController.navigate(R.id.action_to_fragment_my_profile, bundle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Meu Canal";
            String description = "Descrição do meu canal";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);

            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(Notification notification) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Solicitação de amizade")
                .setContentText(notification.getMessage())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        notificationManager.notify(1, builder.build());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
