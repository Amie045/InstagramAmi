package com.ami.instagramami

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ami.instagramami.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener(bottomnavListener)
         val fragmentt = supportFragmentManager.beginTransaction()
         fragmentt.add(R.id.frag_countainer, HomeFragment())
         fragmentt.commit()

    }
    private val bottomnavListener = BottomNavigationView.OnNavigationItemSelectedListener { a -> var bottomnav: Fragment = HomeFragment()
        when(a.itemId) {
            R.id.itemHome -> {
                bottomnav = HomeFragment()
            }
            R.id.itemSearch -> {
                bottomnav = SearchFragment()
            }
            R.id.itemProfile -> {
                bottomnav = ProfileFragment()
            }
            R.id.itemActivity -> {
                bottomnav = ActivityFragment()
            }
            R.id.itemAddPost -> {
                startActivity(Intent(this, AddPostActivity::class.java))
            }
        }
        val fragmentt = supportFragmentManager.beginTransaction()
        fragmentt.replace(R.id.frag_countainer,bottomnav)
        fragmentt.commit()

        true
    }

}