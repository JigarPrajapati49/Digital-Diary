package com.rayo.digitaldiary.ui.product

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemProductBinding
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class ProductAdapter(
    private val productList: List<Product>,
    private val currencySymbol: String,
    private val presenter: ProductPresenter,
    private val languageTranslationList: List<LanguageTranslationData> = ArrayList(),
) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) {
            with(binding) {
                val test = translateUnit(item.unit)
                productUnit = test
                item.price =item.price.toFloat().toString()
                productData = item
                currencySymbol = this@ProductAdapter.currencySymbol
                presenter = this@ProductAdapter.presenter
                executePendingBindings()
            }
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
        }catch (e:Exception){
            Log.e("TAG", "translateUnit: -${e.message}" )
        }
        return translatedValue
    }
}