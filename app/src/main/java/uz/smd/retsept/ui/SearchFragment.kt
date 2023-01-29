package uz.smd.retsept.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_search.*
import uz.smd.retsept.utils.changeLocale
import android.app.SearchManager
import android.content.Context
import androidx.appcompat.widget.SearchView
import uz.smd.retsept.R
import uz.smd.retsept.ui.base.MainActivity
import uz.smd.retsept.ui.base.MainViewModel

/**
 * Created by Siddikov Mukhriddin on 9/10/21
 */
class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var viewModel: MainViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        initSearch()
        btnSearch.setOnClickListener {
            (requireActivity() as MainActivity).seacrh(etSearch.query.toString())
        }
        initLocale()
        btnMain.setOnClickListener {
            (requireActivity() as MainActivity).openNavView()
        }
    }

    private fun initSearch() {
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager?
        if (searchManager != null) {
            etSearch.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        }
        etSearch.setIconifiedByDefault(false);
        etSearch.isQueryRefinementEnabled = true;
        etSearch.requestFocus(1);
        etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                (requireActivity() as MainActivity).seacrh(query)
                return true
            }
            override fun onQueryTextChange(newText: String?)=true
        })

    }

    private fun initLocale() {
        viewModel.changeLan(btnRu.isChecked)
        requireActivity().changeLocale("en")
        rdLang.setOnCheckedChangeListener { radioGroup, i ->
            if (btnRu.isChecked)
                requireActivity().changeLocale("ru")
            else
                requireActivity().changeLocale("en")
            viewModel.changeLan(btnRu.isChecked)
        }
    }
}