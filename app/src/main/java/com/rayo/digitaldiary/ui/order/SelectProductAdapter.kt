package com.rayo.digitaldiary.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemSelectProductBinding
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class SelectProductAdapter(
    private val productList: List<Product>,
    private val currencySymbol: String,
    private val languageTranslationList : List<LanguageTranslationData> = ArrayList()
) : RecyclerView.Adapter<SelectProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSelectProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun getSelectedProduct(): List<Product> {
        val selectedProductData: MutableList<Product> = ArrayList()
        for (item in productList) {
            if (item.isProductSelected == 1) {
                selectedProductData.add(item)
            }
        }
        return selectedProductData
    }

    inner class ViewHolder(private val binding: ItemSelectProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mProductData: Product) {
            with(binding) {
                constraintProductMain.setOnClickListener { view ->
                    productList[adapterPosition].isProductSelected =
                        if (productList[adapterPosition].isProductSelected == 1) 0 else 1
                    notifyItemChanged(adapterPosition)
                }
                val test = translateUnit(mProductData.unit)
                productUnit = test
                productData = mProductData
                currencySymbol = this@SelectProductAdapter.currencySymbol
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