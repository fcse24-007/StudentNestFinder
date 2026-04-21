package com.example.studentnestfinder.ui.xml

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.studentnestfinder.R
import com.example.studentnestfinder.db.AppDatabase
import kotlinx.coroutines.launch

class ListingDetailFragment : Fragment(R.layout.fragment_listing_detail) {
    interface Callbacks {
        fun onBackToHome()
    }

    private var callbacks: Callbacks? = null

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
        val listingId = requireArguments().getInt(ARG_LISTING_ID)
        val title = view.findViewById<TextView>(R.id.detailTitle)
        val meta = view.findViewById<TextView>(R.id.detailMeta)
        val description = view.findViewById<TextView>(R.id.detailDescription)
        val backButton = view.findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener { callbacks?.onBackToHome() }

        val listingDao = AppDatabase.getInstance(requireContext().applicationContext).listingDao()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listingDao.getById(listingId).collect { listing ->
                    if (listing == null) {
                        title.text = getString(R.string.listing_not_found)
                        meta.text = ""
                        description.text = ""
                    } else {
                        title.text = listing.title
                        meta.text = getString(
                            R.string.listing_meta,
                            listing.location,
                            listing.type,
                            listing.price.toInt(),
                            listing.status
                        )
                        description.text = listing.description
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_LISTING_ID = "listing_id"

        fun newInstance(listingId: Int): ListingDetailFragment = ListingDetailFragment().apply {
            arguments = Bundle().apply { putInt(ARG_LISTING_ID, listingId) }
        }
    }
}
