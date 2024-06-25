package com.example.poupaai.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.poupaai.R;
import com.example.poupaai.entities.User;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private List<User> friendList;
    private User loggedUser;
    private NavController navController;
    private Context context;

    public FriendAdapter(List<User> friendList, User loggedUser, NavController navController, Context context) {
        this.friendList = friendList;
        this.loggedUser = loggedUser;
        this.navController = navController;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_friend, parent, false
        );
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder holder, int position) {
        User friend = friendList.get(position);
        holder.bind(friend);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", loggedUser);
                bundle.putParcelable("friend", friend);

                navController.navigate(R.id.action_to_fragment_expenses_friends, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendEmail;
        ImageView friendPhotoProfile;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            friendName = itemView.findViewById(R.id.tvFriendName);
            friendEmail = itemView.findViewById(R.id.tvEmailFriend);
            friendPhotoProfile = itemView.findViewById(R.id.ivImgProfileFriend);
        }

        public void bind(User friend) {
            friendName.setText(friend.getUsername());
            friendEmail.setText(friend.getEmail());
            if (friend.getProfileImagePath() != null) {
                Glide.with(context)
                        .load(Uri.parse(friend.getProfileImagePath()))
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(friendPhotoProfile);
            }
        }
    }
}
