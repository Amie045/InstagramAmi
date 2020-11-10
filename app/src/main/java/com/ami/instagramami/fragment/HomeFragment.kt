package com.ami.instagramami.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ami.instagramami.R
import com.ami.instagramami.adapter.PostAdapter
import com.ami.instagramami.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewhome = inflater.inflate(R.layout.fragment_home, container,false)
        var recyclerViewHome: RecyclerView? = null
        recyclerViewHome =viewhome.findViewById(R.id.recycler_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerViewHome.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context.let { it?.let { it1 -> PostAdapter (it1, postList as ArrayList<Post>) }}
        recyclerViewHome.adapter = postAdapter

        cekFollowing()

        return viewhome
    }

    private fun cekFollowing() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<*>).clear()

                    for (s in snapshot.children) {
                        s.key.let { it?.let { it1 -> (followingList as ArrayList<String>).add(it1) } }
                    }
                    getPost()
                }
            }
        })
    }

    private fun getPost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                for (s in snapshot.children) {
                    val post = s.getValue(Post::class.java)

                    for (id in (followingList as ArrayList<String>)) {
                        if (post!!.getPublisher() == id){
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


}

