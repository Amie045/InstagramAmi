package com.ami.instagramami

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txt_Signin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        btn_Regis.setOnClickListener {
            createAccount()
        }






    }

    private fun createAccount() {
        val fullname = fullname_regis.text.toString()
        val username = usrnmRegis.text.toString()
        val email    = email_regis.text.toString()
        val password = Password_regis.text.toString()

        when{
            TextUtils.isEmpty(fullname) -> Toast.makeText(this, "Fullname is Required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username) -> Toast.makeText(this, "Username is Required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email)    -> Toast.makeText(this, "Email is Required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is Required", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("Please Wait...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful)
                            saveUserInfo(fullname, username, email, password, progressDialog)
                    }
            }
        }
    }

    private fun saveUserInfo(fullname: String, username: String, email: String, password: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullname
        userMap["username"] = username
        userMap["email"] = email
        userMap["bio"] = "Hey I'm Student at Boarding School"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagramclone-93319.appspot.com/o/Default%20Images%2Fsukarno.png?alt=media&token=58e29fb0-c5af-43f9-b834-fc4c7b503ee5"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this, "Akun Sudah Dibuat", Toast.LENGTH_SHORT).show()

                    val pergi = Intent(this@RegisterActivity, MainActivity::class.java)
                    pergi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(pergi)
                    finish()
                }else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}