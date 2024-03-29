package com.rayo.digitaldiary.ui.product

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.api.APIRepository
import com.rayo.digitaldiary.api.ApiException
import com.rayo.digitaldiary.api.NoInternetException
import com.rayo.digitaldiary.api.UnauthorizedUserException
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.database.ProductDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**po
 * Created by Mittal Varsani on 29/04/22.
 *
 * @author Mittal Varsani
 */

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val apiRepository: APIRepository,
    private val preferenceManager: PreferenceManager,
    private val productDao: ProductDao,
    private val languageTranslationDao: LanguageTranslationDao,
) :
    BaseViewModel() {
    val addProductSuccess = MutableLiveData<AddProductResponse>()
    val languageTranslationData = MutableLiveData<List<LanguageTranslationData>>()

    fun callAddOrUpdateProduct(product: Product) {
        callCreateOrUpdateProduct(product)
    }

    private fun callCreateOrUpdateProduct(product: Product) {
        Coroutines.ioThenMain({
            try {
                callAPI(
                    apiRepository.createOrUpdateProduct(
                        preferenceManager.getPref(Constants.prefToken, ""),
                        preferenceManager.getPref(
                            Constants.prefLanguageCode,
                            Constants.languageCodeDefault
                        ),
                        product
                    )
                )
            } catch (e: ApiException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: NoInternetException) {
                errorMessage.postValue(Event(e.message))
            } catch (e: UnauthorizedUserException) {
                unauthorizedUserHandler.postValue(Event(e.message))
            }
        }, {
            if (it != null && it is AddProductResponse) {
                addProductToDatabase(it)
            }
        })
    }

    private fun addProductToDatabase(addProductResponse: AddProductResponse) {
        Coroutines.ioThenMain({
            addProductResponse.productData?.let { productDao.insertProduct(it) }
        }, {
            addProductSuccess.postValue(addProductResponse)
        })
    }

    suspend fun getLanguageTranslation(): List<LanguageTranslationData> {
        return languageTranslationDao.getLanguageTranslation(preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault))
    }
}