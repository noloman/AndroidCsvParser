package com.nulltwenty.rabobankcsvparser.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.nulltwenty.rabobankcsvparser.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ParserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect {
                val adapter = UserListAdapter()
                recyclerView.adapter = adapter
                adapter.submitList(it.userList)
            }
        }
    }
}
