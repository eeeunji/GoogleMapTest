package com.example.googlemaptest.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.googlemaptest.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        val title = arguments?.getString("title")
        view.findViewById<TextView>(R.id.tv_title).text = title

        val snippet = arguments?.getString("snippet")
        view.findViewById<TextView>(R.id.tv_snippet).text = snippet
        
        return view
    }


}