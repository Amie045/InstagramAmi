package com.ami.instagramami.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.ami.instagramami.R
import com.ami.instagramami.model.Comment
import com.ami.instagramami.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.events.Publisher
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext : Context, private val mComment : MutableList<Comment> ) : RecyclerView.Adapter<CommentsViewHolder>() {
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_comment, parent,false)


        return CommentsViewHolder(view)

    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment[position]

        holder.comment_nan.text = comment.getComment()
        getUserInfo(holder.imageProfileComment, holder.userNameComment, comment.getPublisher())


    }

    private fun getUserInfo(imageProfileComment: CircleImageView, userNameComment: TextView, publisher: String){
        val userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(publisher)


        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)

                    Picasso.get()
                        .load(user!!.getImage())
                        .into(imageProfileComment)
                    userNameComment.text = user!!.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }





}

class CommentsViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

    var imageProfileComment : CircleImageView = itemView.findViewById(R.id.profile_image_comment)
    var userNameComment : TextView = itemView.findViewById(R.id.username_comment)
    var comment_nan: TextView = itemView.findViewById(R.id.textViewComment)
}
