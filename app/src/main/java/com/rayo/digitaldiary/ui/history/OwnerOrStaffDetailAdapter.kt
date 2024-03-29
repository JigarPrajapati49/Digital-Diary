package com.rayo.digitaldiary.ui.history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.DigitalDiaryApplication.Companion.languageTranslationList
import com.rayo.digitaldiary.databinding.ItemOrderProductBinding
import com.rayo.digitaldiary.ui.languageTranslation.LanguageTranslationData
import com.rayo.digitaldiary.ui.product.Product

/**
 * Created by Mittal Varsani on 13/06/23.
 *
 * @author Mittal Varsani
 */
class OwnerOrStaffDetailAdapter(
    private val productList: List<Product>,
    private val currencySymbol: String,
    private val languageTranslationData: List<LanguageTranslationData> = ArrayList(),
) : RecyclerView.Adapter<OwnerOrStaffDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(private val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            with(binding) {
                currencySymbol = this@OwnerOrStaffDetailAdapter.currencySymbol
                val unit = translateUnit(product.unit)
                productUnit = unit
                productData = product
            }
        }

        fun translateUnit(term: String): String {
            var translatedValue = ""
            try {
                for (i in languageTranslationData) {
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