package com.example.nerdle

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nerdle.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        topAppBar = findViewById(R.id.tool_bar)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_home -> {
                    // TODO: navigate to GameFragment
                    true
                }
                R.id.action_instruction -> {
                    val instructionLayout = layoutInflater.inflate(
                        R.layout.instruction, null
                    )
                    val scrollView = instructionLayout.findViewById<ScrollView>(
                        R.id.instruction_scrollview
                    )
                    // Problem: java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
                    // Solution: https://www.jianshu.com/p/d43d0ac7e2f7
                    val scrollViewParent = scrollView.parent as ViewGroup
                    scrollViewParent.removeView(scrollView)

                    MaterialAlertDialogBuilder(this)
                        //.setMessage(R.string.instruction)
                        .setView(scrollView)
                        .setTitle("How to play mini Nerdle").show()
                    true
                }
                R.id.action_settings -> {
                    // TODO: show settings
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}