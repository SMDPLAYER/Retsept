package uz.smd.retsept.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_search_result.*
import kotlinx.android.synthetic.main.fragment_search_result.btnMain
import kotlinx.android.synthetic.main.fragment_search_result.etSearch
import kotlinx.android.synthetic.main.layout_filter.view.*
import uz.smd.retsept.utils.dpToPxInt
import uz.smd.retsept.utils.hideKeyboard
import uz.smd.retsept.R
import uz.smd.retsept.ui.base.MainActivity
import uz.smd.retsept.ui.base.MainViewModel

/**
 * Created by Siddikov Mukhriddin on 9/10/21
 */
@SuppressLint("FragmentLiveDataObserve")
class SearchResultFragment : Fragment(R.layout.fragment_search_result) {
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        handleClick()
        initSearch()

    }

    private fun handleClick() {
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        btnMain.setOnClickListener {
            (requireActivity() as MainActivity).openNavView()
        }

        btnClear.setOnClickListener {
            etSearch.setText("")
        }
    }

    private fun initSearch() {
        etSearch.setText(viewModel.searchId)
        etSearch.doAfterTextChanged {
            viewModel.searchId = etSearch.text.toString()
            viewModel.search()
        }

        viewModel.searchResult.observe(this) { searchResults ->
            resultHolder.removeAllViews()
            searchResults?.forEach { searchRes ->
                resultHolder.addView(LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPxInt(1)
                        )
                        setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.lineColor
                            )
                        )

                    })
                    addView(TextView(requireContext()).apply {
                        setPadding(dpToPxInt(15), dpToPxInt(10), dpToPxInt(15), dpToPxInt(10))
                        text = searchRes.title
                        textSize = 16f
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.tvColor))
                    })
                    setOnClickListener {
                        hideKeyboard()
                    }
                })
            }
        }
    }





}