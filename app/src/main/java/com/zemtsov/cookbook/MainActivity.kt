package com.zemtsov.cookbook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zemtsov.cookbook.bottomnavigationcompound.BottomNavigationFragment
import com.zemtsov.cookbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        supportFragmentManager
            .beginTransaction()
            .add(viewBinding.containerViewGroup.id, BottomNavigationFragment())
            .commit()
    }
}
