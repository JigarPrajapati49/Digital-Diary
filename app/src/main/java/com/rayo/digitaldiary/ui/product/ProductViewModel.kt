package com.rayo.digitaldiary.ui.product

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.database.ProductDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Mittal Varsani on 29/04/22.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val productDao: ProductDao,
    private val languageTranslationDao: LanguageTranslationDao,
    private val preferenceManager: PreferenceManager
) :
    BaseViewModel() {
    val productList = MutableLiveData<MutableList<Product>>()
    var filterList: MutableList<Product> = ArrayList()
    var filterProductList = Constants.CREATED_AT

    fun getAllProductFromDatabase() {
        Coroutines.ioThenMain({
            Log.d("TAG", "getAllProductFromDatabase: Product startTime")
            productDao.getProducts()
        }, {
            Log.d("TAG", "getAllProductFromDatabase: Product endTime")
            if (it != null) {
                filterList.clear()
                filterList.addAll(it)
            }
            filterProduct(filterProductList)
        })
    }

    suspend fun getLanguageTranslation(): List<LanguageTranslationData> {
        return languageTranslationDao.getLanguageTranslation(preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault))
    }

    fun filterProduct(status: String): List<Product> {
        val list: List<Product> =
            when (status) {
                Constants.A_TO_Z -> { // Name A to Z
                    filterList.sortedBy { it.name }
                }
                Constants.Z_TO_A -> { // Name Z to A
                    filterList.sortedBy { it.name }.reversed()
                }
                else -> {
                    filterList // created - at
                }
            }
        productList.postValue(list.toMutableList())
        return list
    }

}