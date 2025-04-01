package com.example.customerlauncher.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.example.customerlauncher.R

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commitNow()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.main_browse_fragment)
        if (fragment is MainFragment) {
            if (fragment.handleKeyDown(keyCode)) {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}



