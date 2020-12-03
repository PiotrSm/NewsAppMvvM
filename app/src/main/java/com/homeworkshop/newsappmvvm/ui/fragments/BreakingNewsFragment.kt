package com.homeworkshop.newsappmvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.homeworkshop.newsappmvvm.R
import com.homeworkshop.newsappmvvm.adapters.NewsAdapter
import com.homeworkshop.newsappmvvm.ui.MainActivity
import com.homeworkshop.newsappmvvm.ui.NewsViewModel
import com.homeworkshop.newsappmvvm.util.Constants.Companion.QUERY_PAGE_SIZE
import com.homeworkshop.newsappmvvm.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*


class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                    R.id.action_breakingNewsFragment_to_articleFragment,
                    bundle
            )
        }

        viewModel.brekingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())//musimy to konwertować do normalnel listy(nie Mutable) bo inaczej differ nie updatuje listy w RecyclerView
                        //sprawdzamy czy wgraliśmy ostatnią stronę
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE +2
                        isLastPage = viewModel.breakingNewsPage == totalPages
//                        Log.d(TAG,"Total pages: ${newsResponse.totalResults} , breakingNewsPage ${viewModel.breakingNewsPage}")
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)//jeżeli to jest ostatnia strona to resetujemy padding
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error accured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    //------------- WGRYWANIE NOWEJ STRONY GDY DOJDZIEMY DO KOŃCA LISTY -------------------

    //zmienne potrzebne do obsługi wgrywania nowej strony jak dojdziemy do ostatniego elementu listy(pagination)
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //wykrywamy czy scrolujemy
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //obliczamy czy jesteśmy przy ostatnim elemencie
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            //sprawdzamy czy ostatni element jest widoczny
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            //sprawdzamy czy nie jesteśmy na początku
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            //sprawdzamy czy musimy wgrywać nową stronę z API prowidera
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            //jeżeli zajdą wszystkie warunki to wgrywamy nowe elementy z serwisu API
            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }

    }

}