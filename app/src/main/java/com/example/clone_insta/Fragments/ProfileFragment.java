package com.example.clone_insta.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clone_insta.Adapter.PhotoAdapter;
import com.example.clone_insta.Adapter.PostAdapter;
import com.example.clone_insta.Model.Post;
import com.example.clone_insta.Model.User;
import com.example.clone_insta.R;
import com.google.auto.value.AutoAnnotation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> mPhotoList;

    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdaptersaves;
    private List<Post> mSavedPost;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView bio;
    private TextView fullname;
    private TextView following;

    private ImageView myPictures;
    private ImageView savedPictures;
    private TextView username;
    private String profileId;
    private Button editProfile;
    FirebaseUser fUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        fUser= FirebaseAuth.getInstance().getCurrentUser();


        editProfile=view.findViewById(R.id.edit_profile);
        imageProfile=view.findViewById(R.id.image_profile1);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.posts);
        imageProfile=view.findViewById(R.id.image_profile);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.username);
        myPictures=view.findViewById(R.id.my_pictures);
        savedPictures=view.findViewById(R.id.saved_pictures);
        fullname=view.findViewById(R.id.fullname);
        profileId=fUser.getUid();

        recyclerViewSaves=view.findViewById(R.id.recycler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(),3));
        mSavedPost=new ArrayList<>();
        postAdaptersaves=new PhotoAdapter(getContext(),mSavedPost);
        recyclerViewSaves.setAdapter(postAdaptersaves);

        recyclerView=view.findViewById(R.id.recycler_view_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        mPhotoList=new ArrayList<>();
        photoAdapter=new PhotoAdapter(getContext(),mPhotoList);
        recyclerView.setAdapter(photoAdapter);

        String data=getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileid","none");
        if(data.equals("none")) {
            profileId = fUser.getUid();
        }else{
            profileId=data;
            getContext().getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileid","none").apply();
        }
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();

        myPhotos();
        getSavedPosts();

        if(profileId.equals(fUser.getUid())){
            editProfile.setText("Edit Profile");
        }
        else {
            checkFollowingStatus();
        }
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buttonText=editProfile.getText().toString();
                if(buttonText.equals("Edit Profile")){
                    //redirect Edit profile
                }
                else if(buttonText.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileId).child("followers").child(fUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(profileId).child("followers").child(fUser.getUid()).removeValue();
                }
            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);
        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });
        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }

    private void getSavedPosts() {
        final List<String> savedIds=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    savedIds.add(snapshot.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mSavedPost.clear();
                        for(DataSnapshot snapshot1: dataSnapshot.getChildren()){
                            Post post=snapshot1.getValue(Post.class);
                            for(String id: savedIds){
                                if( post.getPostId().equals(id)){
                                    mSavedPost.add(post);
                                }
                            }
                        }
                        postAdaptersaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPhotoList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        mPhotoList.add(post);
                    }
                    Collections.reverse(mPhotoList);
                    photoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileId).exists()){
                    editProfile.setText("following");
                }
                else{
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    Post post= snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)) counter++;
                }
                posts.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                //Toast.makeText(getContext(), user.toString(), Toast.LENGTH_SHORT).show();
                if(!user.getImageurl().equals("default"))
                    Picasso.get().load(user.getImageurl()).into(imageProfile);
                username.setText(user.getUesrName());
                fullname.setText(user.getName());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
