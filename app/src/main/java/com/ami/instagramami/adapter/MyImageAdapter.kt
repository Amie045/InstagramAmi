package com.ami.instagramami.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ami.instagramami.R
import com.ami.instagramami.model.Post
import com.bumptech.glide.Glide
import com.google.firebase.database.core.Context

class MyImageAdapter (private val myContext: android.content.Context, private val mPost : List<Post>) : RecyclerView.Adapter<MyImagesViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesViewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_post_layout, parent,false)
        return MyImagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: MyImagesViewHolder, position: Int) {
        val mPost = mPost[position]

        Glide.with(myContext).load(mPost.getPostImage()).into(holder.postImageGrid)

    }

}

class MyImagesViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var postImageGrid : ImageView = itemView.findViewById(R.id.post_image_grid)

}
