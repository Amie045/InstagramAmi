package com.ami.instagramami

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.ami.instagramami.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checkInfoProfile = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicture: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicture = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        txt_edit_photo_profile.setOnClickListener {
            checkInfoProfile = "Clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@EditProfileActivity)
        }

        btn_save_edit_profile.setOnClickListener {
            if (checkInfoProfile == "Clicked"){
                uploadImageProfileAndUpdateInfoProfile()
            }else{
                updateUserInfoOnly()
            }
        }
        userInfo()
    }

    private fun uploadImageProfileAndUpdateInfoProfile() {
        when{
            imageUri == null -> Toast.makeText(this, "Select Image....", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(set_fullname_profil.text.toString()) ->{
                Toast.makeText(this, "Jangan Kosong", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(set_username_profil.text.toString()) ->{
                Toast.makeText(this, "Jangan Kosong", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(set_bio_profile.text.toString()) ->{
                Toast.makeText(this, "Jangan Kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this@EditProfileActivity)
                progressDialog.setTitle("Update Profile")
                progressDialog.setMessage("Tunngu, Lagi Update")
                progressDialog.show()

                val fileRef = storageProfilePicture!!.child(firebaseUser.uid + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation  <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful){
                        task.exception.let {
                            throw it!!

                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val userRef = FirebaseDatabase.getInstance().reference.child("users")
                        val userMap = HashMap<String, Any>()

                        userMap["fullname"] = set_fullname_profil.text.toString()
                        userMap["username"] = set_username_profil.text.toString()
                        userMap["bio"]      = set_bio_profile.text.toString()
                        userMap["image"]    = myUrl

                        userRef.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(this, "info Sudah Di Update", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else{
                        progressDialog.dismiss()
                    }
                })

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            set_image_profile.setImageURI(imageUri)
        }
    }


    private fun updateUserInfoOnly() {
        when{
            TextUtils.isEmpty(set_fullname_profil.text.toString()) ->{
                Toast.makeText(this, "Jangan Kosong", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(set_username_profil.text.toString()) ->{
                Toast.makeText(this, "Jangan Kosong", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(set_bio_profile.text.toString()) ->{
                Toast.makeText(this, "Jangan Kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference
                    .child("users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = set_fullname_profil.text.toString()
                userMap["username"] = set_username_profil.text.toString()
                userMap["bio"]      = set_bio_profile.text.toString()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Info sudah di update", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }

        }
    }



    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)

                    set_fullname_profil.setText(user?.getFullname())
                    set_username_profil.setText(user?.getUsername())
                    set_bio_profile.setText(user?.getBio())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}