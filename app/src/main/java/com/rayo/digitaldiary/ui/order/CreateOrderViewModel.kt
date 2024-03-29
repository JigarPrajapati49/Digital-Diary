package com.rayo.digitaldiary.ui.order

import androidx.lifecycle.MutableLiveData
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.baseClasses.Event
import com.rayo.digitaldiary.database.CustomerDao
import com.rayo.digitaldiary.database.LanguageTranslationDao
import com.rayo.digitaldiary.database.OrderDao
import com.rayo.digitaldiary.database.ProductDao
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.Coroutines
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.helper.Utils
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao,
    private val languageTranslationDao: LanguageTranslationDao,
    private val preferenceManager: PreferenceManager,
) : BaseViewModel() {

    val orderInsertedInDB = MutableLiveData<Event<OrderData>>()
    val customerList = MutableLiveData<List<CustomerData>>()
    val productList = MutableLiveData<List<Product>>()
    var selectedDate: Long = 0
    var selectedCustomerData: CustomerData? = null
    var totalOrderAmount = 0f

    init {
        getAllProductFromDatabase()
        getAllCustomerFromDatabase()
    }

    private fun getAllCustomerFromDatabase() {
        Coroutines.ioThenMain({
            customerDao.getActiveCustomers()
        }, {
            customerList.postValue(it)
        })
    }

    private fun getAllProductFromDatabase() {
        Coroutines.ioThenMain({
            productDao.getActiveProducts()
        }, {
            productList.postValue(it)
        })
    }

    suspend fun getLanguageTranslation(): List<LanguageTranslationData> {
        return languageTranslationDao.getLanguageTranslation(preferenceManager.getPref(Constants.prefLanguageCode, Constants.languageCodeDefault))
    }

    private fun insertOrderInDatabase(orderData: OrderData) {
        Coroutines.ioThenMain({
            orderDao.insertOder(orderData)
        }, {
            orderInsertedInDB.postValue(Event(orderData))
        })
    }

    fun getSelectedCustomerData(customerId: String) {
        customerList.value?.let {
            for (item in it) {
                item.isCustomerSelected = 0
                if (item.id == customerId) {
                    item.isCustomerSelected = 1
                    selectedCustomerData = item
                }
            }
        }
    }

    fun checkSelectedProduct(selectedProductList: List<Product>): List<Product> {
        productList.value?.let {
            for (item in it) {
                for (selectedProduct in selectedProductList) {
                    if (item.id == selectedProduct.id) {
                        item.isProductSelected = 1
                        item.quantity = selectedProduct.quantity
                    }
                }
            }
        }
        return selectedProductList
    }

    fun createOrder(orderData: OrderData) {
        with(orderData) {
            localOrderId = Utils.generateUUID()
            createdById = preferenceManager.getPref(Constants.prefUserId, "")
            orderDate = Utils.formatDateToUTC(selectedDate)
            customerId = selectedCustomerData?.id.toString()
            products = Utils.getOrderJson(orderData.product)
            orderAmount = totalOrderAmount.toString()
            createdAt = Utils.getCurrentDateTime()
            updatedAt = createdAt
            sync = 0
            note = orderData.note
            insertOrderInDatabase(this)
        }
    }
}