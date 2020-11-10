package com.ami.instagramami.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.instagramami.EditProfileActivity
import com.ami.instagramami.R
import com.ami.instagramami.adapter.MyImageAdapter
import com.ami.instagramami.model.Post
import com.ami.instagramami.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class ProfileFragment : Fragment() {

    private lateinit var profilId: String
    private lateinit var firebaseUser: FirebaseUser

    var postListGrid: MutableList<Post>? = null
    var myImageAdapter: MyImageAdapter? = null
//    private var mRecyclerViewImg: RecyclerView? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref !=null){
            this.profilId = pref.getString("profileId", "none")!!

        }
        if (profilId == firebaseUser.uid){
            view?.EditProfile?.text = "Edit Profile"
        } else if (profilId != firebaseUser.uid){
            cekFollowAndFollowingStatus()
        }

        viewProfile.EditProfile.setOnClickListener {

            val getbuttonText =view?.EditProfile?.text.toString()

            when{
                getbuttonText == "Edit Profile" -> startActivity(Intent(context, EditProfileActivity::class.java))


                getbuttonText == "Follow" -> {
                    firebaseUser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(profilId).setValue(true)
                    }

                    firebaseUser.uid.let { it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Followers").child(profilId).setValue(true)
                    }

                }

                getbuttonText == "Following" ->{
                    firebaseUser.uid.let { it ->
                        firebaseUser.uid.let {it ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it.toString())
                                .child("Following").child(profilId).removeValue()
                        }
                        firebaseUser.uid.let {it ->
                            FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it.toString())
                                .child("Followers").child(profilId).removeValue()
                        }
                    }
                }


            }

        }

        getFollowers()
        getFollowing()
        userInfo()
        return viewProfile
    }



    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(profilId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).into(view?.profile_pic)
                    view?.profile_username?.text =user?.getUsername()
                    view?.txt_profile_fullname?.text = user?.getFullname()
                    view?.bio?.text = user?.getBio()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profilId)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.total_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profilId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.totalfolower?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun cekFollowAndFollowingStatus() {
        val followingRef = firebaseUser.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        view?.EditProfile?.text = "Following"
                    }else {
                        view?.EditProfile?.text = "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }



}