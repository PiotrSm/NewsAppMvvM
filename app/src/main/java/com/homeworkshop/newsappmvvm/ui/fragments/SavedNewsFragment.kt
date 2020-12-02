package com.homeworkshop.newsappmvvm.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.homeworkshop.newsappmvvm.R
import com.homeworkshop.newsappmvvm.ui.MainActivity
import com.homeworkshop.newsappmvvm.ui.NewsViewModel

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {


    lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
    }

}