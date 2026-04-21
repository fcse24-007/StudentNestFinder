package com.example.studentnestfinder.ui.xml

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studentnestfinder.R
import com.example.studentnestfinder.db.entities.Listing
import com.example.studentnestfinder.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    interface Callbacks {
        fun onListingSelected(listingId: Int)
        fun onLogout()
    }

    private val viewModel: HomeViewModel by viewModels()
    private var callbacks: Callbacks? = null
    private var listings: List<Listing> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val listView = view.findViewById<ListView>(R.id.listingsList)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)
        val errorText = view.findViewById<TextView>(R.id.errorText)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)

        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            listings.getOrNull(position)?.let { callbacks?.onListingSelected(it.id) }
        }

        logoutButton.setOnClickListener { callbacks?.onLogout() }
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                viewModel.onSearchQueryChanged(s?.toString().orEmpty())
            override fun afterTextChanged(s: Editable?) = Unit
        })

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    listings = state.listings
                    adapter.clear()
                    adapter.addAll(
                        state.listings.map {
                            getString(
                                R.string.listing_item_format,
                                it.title,
                                it.price.toInt(),
                                it.location
                            )
                        }
                    )
                    adapter.notifyDataSetChanged()

                    val showError = !state.error.isNullOrBlank()
                    val showEmpty = state.listings.isEmpty() && !state.isLoading && !showError
                    emptyText.isVisible = showEmpty
                    errorText.isVisible = showError
                    errorText.text = state.error.orEmpty()
                }
            }
        }
    }
}
