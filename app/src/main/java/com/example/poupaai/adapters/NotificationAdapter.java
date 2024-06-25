package com.example.poupaai.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.Notification;
import com.example.poupaai.entities.User;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private LocalDatabase db;
    private List<Notification> notificationList;
    private User loggedUser;
    private NavController navController;

    public NotificationAdapter(List<Notification> notificationList, LocalDatabase db, User loggedUser, NavController navController) {
        this.notificationList = notificationList;
        this.db = db;
        this.loggedUser = loggedUser;
        this.navController = navController;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_notification, parent, false
        );
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.bind(notification);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("notification", notification);

                navController.navigate(R.id.action_to_fragment_notification, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationMessage;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
        }

        public void bind(Notification notification) {
            notificationMessage.setText(notification.getMessage());
        }
    }
}
