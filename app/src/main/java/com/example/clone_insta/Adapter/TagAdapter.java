package com.example.clone_insta.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clone_insta.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{
    Context mContext;
    List<String> mTags,mTagsCount;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagsCount) {
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.tag_item,parent,false);
        return new TagAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#"+mTags.get(position));
        holder.noOfPosts.setText(mTagsCount.get(position));
    }
    @Override
    public int getItemCount() {
        return mTags.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public  TextView tag,noOfPosts;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag=itemView.findViewById(R.id.hash_tag);
            noOfPosts=itemView.findViewById(R.id.no_of_posts);

        }
    }
    public void filter(List<String> filterTag,List<String> filterTagsCount){
        this.mTags=filterTag;
        this.mTagsCount=filterTagsCount;
        notifyDataSetChanged();
    }
}
