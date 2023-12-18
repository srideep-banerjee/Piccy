package com.example.piccy.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.piccy.databinding.MenuHomeBinding
import com.example.piccy.viewmodels.HomeViewMode

class HomeFragment : Fragment() {
    private lateinit var menuHomeBinding: MenuHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuHomeBinding = MenuHomeBinding.inflate(inflater, container, false)
        menuHomeBinding.lifecycleOwner = viewLifecycleOwner

        val model by viewModels<HomeViewMode>()

        menuHomeBinding.viewModel = model

        val observer = Observer<Int>{newCount ->
            menuHomeBinding.textView.text = "Button clicked $newCount times."
        }

        model.mutableLiveData.observe(viewLifecycleOwner, observer)
        return menuHomeBinding.root
    }
}