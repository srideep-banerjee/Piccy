package com.example.piccy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.piccy.databinding.MenuHomeBinding
import com.example.piccy.viewmodels.HomeViewMode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var menuHomeBinding: MenuHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuHomeBinding = MenuHomeBinding.inflate(inflater, container, false)
        menuHomeBinding.lifecycleOwner = this

        val model by viewModels<HomeViewMode>()

        menuHomeBinding.viewModel = model

        val observer = Observer<Int>{newCount ->
            menuHomeBinding.textView.text = "Button clicked $newCount times."
        }

        model.mutableLiveData.observe(viewLifecycleOwner, observer)
        return menuHomeBinding.root
    }
}