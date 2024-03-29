package com.rayo.digitaldiary.ui.product

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseFragment
import com.rayo.digitaldiary.databinding.FragmentProductListingBinding
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
@AndroidEntryPoint
class ProductListingFragment : BaseFragment<FragmentProductListingBinding, ProductViewModel>(),
    ProductPresenter, PopupMenu.OnMenuItemClickListener {

    private var allProductListing: MutableList<Product> = ArrayList()
    private var displayProductListing: MutableList<Product> = ArrayList()
    private var languageList: ArrayList<LanguageTranslationData> = ArrayList()
    override val viewModel: ProductViewModel by viewModels()

    override fun getFragmentId(): Int {
        return R.layout.fragment_product_listing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.productPresenter = this

        binding.editVisibility = if (activity is OwnerMainActivity) {
            View.VISIBLE
        } else {
            View.GONE
        }

        Coroutines.ioThenMain({
            viewModel.getLanguageTranslation().let {
                languageList.clear()
                languageList.addAll(it)
            }
        }, {
            setProductAdapter()
        })

        showProgressDialog()
        viewModel.getAllProductFromDatabase()

        viewModel.productList.observe(viewLifecycleOwner) {
            dismissProgressDialog()
            allProductListing.clear()
            allProductListing.addAll(it)
            setProductData()
        }
        initFilterMenu()
    }

    private fun setProductData() {
        when (binding.toggleGroup.checkedButtonId) {
            R.id.btnAll -> {
                onAllClick()
            }
            R.id.btnActive -> {
                onActiveClick()
            }
            else -> {
                onInactiveClick()
            }
        }
    }

    private fun setProductAdapter() {
        binding.productAdapter = ProductAdapter(
            displayProductListing,
            preferenceManager.getPref(Constants.prefCurrencySymbol, ""),
            this,
            languageList
        )
    }

    private fun applyStaffFilter(states: Int) {
        displayProductListing.clear()
        if (states == Constants.STATES_ALL) {
            displayProductListing.addAll(allProductListing)
        } else {
            displayProductListing.addAll(allProductListing.filter { staffData ->
                staffData.active == states
            })
        }
        binding.errorMessage = if (displayProductListing.isEmpty()) {
            getString(R.string.no_product)
        } else {
            ""
        }
        binding.productAdapter?.notifyDataSetChanged()
    }

    override fun onAddProductClick() {
        if (Utils.isSingleClick()) {
            if (networkInterceptor.isInternetAvailable()) {
                AddProductDialogFragment.show(parentFragmentManager, this)
            } else {
                context?.let {
                    dialogHelper.showOneButtonDialog(it, Utils.getTranslatedValue(it.getString(R.string.no_internet)))
                }
            }
        }
    }

    override fun onAllClick() {
        applyStaffFilter(Constants.STATES_ALL)
    }

    override fun onActiveClick() {
        applyStaffFilter(Constants.STATES_ACTIVE)
    }

    override fun onInactiveClick() {
        applyStaffFilter(Constants.STATES_INACTIVE)
    }

    override fun onProductAddedSuccessfully(product: Product) {
        viewModel.productList.value?.add(0, product)
        displayProductListing.add(0, product)
        allProductListing.add(0, product)
        setProductData()
    }

    override fun onProductClick(data: Product) {
        if (Utils.isSingleClick()) {
            if (activity is OwnerMainActivity) {
                AddProductDialogFragment.show(
                    parentFragmentManager,
                    this@ProductListingFragment,
                    data
                )
            }
        }
    }

    override fun onProductUpdatedSuccessfully() {
        setProductData()
    }

    private fun initFilterMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.history_filter_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {

                    R.id.menuFilter -> {
                        if (Utils.isSingleClick()){
                            val popUpMenu = PopupMenu(context, activity?.findViewById(R.id.menuFilter))
                            val menuInflater = popUpMenu.menuInflater
                            menuInflater.inflate(R.menu.customer_filter_menu, popUpMenu.menu)
                            val filterCreatedAt = popUpMenu.menu.findItem(R.id.filterCreatedAt)
                            val filterDueAmountFirst = popUpMenu.menu.findItem(R.id.filterDueAmountFirst)
                            val filterDueAmountLast = popUpMenu.menu.findItem(R.id.filterDueAmountLast)
                            val filterAtoZ = popUpMenu.menu.findItem(R.id.filterAtoZ)
                            val filterZtoA = popUpMenu.menu.findItem(R.id.filterZtoA)
                            filterCreatedAt.title = Utils.getTranslatedValue(getString(R.string.by_registered))
                            filterDueAmountFirst.title = Utils.getTranslatedValue(getString(R.string.due_ascending))
                            filterDueAmountLast.title = Utils.getTranslatedValue(getString(R.string.due_descending))
                            filterAtoZ.title = Utils.getTranslatedValue(getString(R.string.a_to_z))
                            filterZtoA.title = Utils.getTranslatedValue(getString(R.string.z_to_a))

                            filterDueAmountFirst.setVisible(false)
                            filterDueAmountLast.setVisible(false)

                            when (viewModel.filterProductList) {
                                Constants.CREATED_AT -> filterCreatedAt.isChecked = true
                                Constants.A_TO_Z -> filterAtoZ.isChecked = true
                                Constants.Z_TO_A -> filterZtoA.isChecked = true
                            }
                            popUpMenu.setOnMenuItemClickListener(this@ProductListingFragment)
                            popUpMenu.menu.setGroupCheckable(R.id.filterAll, true, false)
                            popUpMenu.show()
                        }
                        return true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        if (Utils.isSingleClick()) {
            viewModel.filterProductList = when (menuItem?.itemId) {

                R.id.filterAtoZ -> {
                    Constants.A_TO_Z
                }

                R.id.filterZtoA -> {
                    Constants.Z_TO_A
                }

                else -> {
                    Constants.CREATED_AT
                }
            }
            viewModel.filterProduct(viewModel.filterProductList)
        }
        return true
    }
}