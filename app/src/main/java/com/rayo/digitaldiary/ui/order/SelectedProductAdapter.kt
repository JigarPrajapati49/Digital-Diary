package com.rayo.digitaldiary.ui.order

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemSelectedProductBinding
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class SelectedProductAdapter(
    private val productList: List<Product>,
    private val currencySymbol: String,
    private val presenter: CreateOrderPresenter,
    private val languageTranslationList: ArrayList<LanguageTranslationData> = ArrayList(),
) : RecyclerView.Adapter<SelectedProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSelectedProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: ItemSelectedProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mProductData: Product) {
            with(binding) {
                createOrderPresenter = this@SelectedProductAdapter.presenter
                currencySymbol = this@SelectedProductAdapter.currencySymbol
                currentPosition = absoluteAdapterPosition
                val pUnit = translateUnit(mProductData.unit)
                productUnit = pUnit
                mProductData.price = mProductData.price.toFloat().toString()
                productData = mProductData
                isLastPosition = bindingAdapterPosition == (productList.size - 1)
                binding.imgAdd.alpha = if (mProductData.quantity.toInt() == 999) 0.3f else 1f
                binding.imgRemove.alpha = if (mProductData.quantity.toInt() == 0) 0.3f else 1f
                etQuantity.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        if (p0?.isNotEmpty() == true) {
                            if (p0.toString().toInt() <= 0) {
                                binding.root.clearFocus() // When notifying adapter, Edittext field should not be in focused mode
                            }
                            presenter.onProductQuantityUpdate(productList)
                        }
                    }
                })
            }
        }

        fun translateUnit(term: String): String {
            var translatedValue = ""
            try {
                for (i in languageTranslationList) {
                    if (term.trim() == i.term) {
                        translatedValue = i.translatedValue
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "translateUnit: -${e.message}")
            }
            return translatedValue
        }
    }
}