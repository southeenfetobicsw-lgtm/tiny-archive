package com.tinyarchive.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tinyarchive.app.core.di.ServiceLocator
import com.tinyarchive.app.core.navigation.Navigator
import com.tinyarchive.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private var _navigator: Navigator? = null

    val navigator: Navigator get() = requireNotNull(_navigator)

    override fun onCreate(savedInstanceState: Bundle?) {
        ServiceLocator.init(applicationContext)
        super.onCreate(savedInstanceState)
        val inflated = ActivityMainBinding.inflate(layoutInflater)
        _binding = inflated
        setContentView(inflated.root)
        _navigator = Navigator(supportFragmentManager, R.id.fragment_container)
        if (savedInstanceState == null) {
            navigator.showSplash()
        }
    }

    override fun onDestroy() {
        _navigator = null
        _binding = null
        super.onDestroy()
    }
}
