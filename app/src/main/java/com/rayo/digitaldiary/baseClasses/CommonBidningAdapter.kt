package com.rayo.digitaldiary.baseClasses

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.helper.ShowMoreLess
import com.rayo.digitaldiary.helper.Utils
import java.util.Calendar
import kotlin.math.abs


/**
 * Created by Mittal Varsani on 21/12/21.
 *
 * @author Mittal Varsani
 */

@BindingAdapter(value = ["imageUrl"], requireAll = true)
fun loadImage(imgView: ImageView, imageUrl: String?) {
    Glide.with(imgView.context).load(imageUrl).into(imgView)
}

@BindingAdapter(value = ["quantity", "price"], requireAll = true)
fun setPriceWithQuantity(textView: TextView, quantity: String, price: String) {
    textView.text = (quantity.ifEmpty { "0" }.toFloat() * price.toFloat()).toString()
}


@BindingAdapter(value = ["date"], requireAll = true)
fun displayDate(textView: TextView, date: String?) {
    try {
        if (date?.isNotEmpty()!!) {
            textView.text = date.let { Utils.getDateFromString(it)?.time?.let { Utils.getFormattedDate(it) } }
        } else {
            Log.e("TAG", "displayDate: ---------date empty")
        }
    } catch (e: Exception) {
        Log.e("TAG", "displayDate: -----------${e.message}")
    }
}

@BindingAdapter(value = ["createdTime"], requireAll = true)
fun displayTime(textView: TextView, createdTime: String?) {
    createdTime?.let {
        textView.text = Utils.getFormattedTime(it)
    }
}

@BindingAdapter(value = ["dateTime"], requireAll = true)
fun displayDateTime(textView: TextView, dateTime: String?) {
    try {
        if (dateTime?.isNotEmpty()!!) {
            textView.text = dateTime.let {
                Utils.getDateFromString(it)?.time?.let {
                    Utils.getFormattedDateTime(it)
                }
            }
        } else {
            Log.e("TAG", "displayDateTime: ---------date empty")
        }
    } catch (e: Exception) {
        Log.e("TAG", "displayDateTime: ---${e.message}")
    }
}

@BindingAdapter(value = ["sessionDateDifference"], requireAll = true)
fun dateDiff(textView: TextView, sessionDateDifference: String?) {
    sessionDateDifference?.let {
        Utils.getDateFromString(it)?.time?.let { sessionDateInLong ->
            val dateInMillis = Calendar.getInstance().timeInMillis - sessionDateInLong
            val days = (dateInMillis / (1000 * 60 * 60 * 24)).toInt()

            textView.rootView.context?.let { mContext ->
                var lastUsed = ""
                when {
                    days == 0 -> {
                        lastUsed = Utils.getTranslatedValue(mContext.getString(R.string.today))
                    }

                    days == 1 -> {
                        lastUsed = Utils.getTranslatedValue(mContext.getString(R.string.yesterday))
                    }

                    days < 7 -> {
                        lastUsed = (days % 7).toString() + " " +
                                Utils.getTranslatedValue(mContext.getString(R.string.days)) +
                                " " + Utils.getTranslatedValue(mContext.getString(R.string.ago))
                    }

                    (days < 30) -> {
                        val weeksCount = days / 7
                        val weekStr = if (weeksCount == 1) {
                            Utils.getTranslatedValue(mContext.getString(R.string.week))
                        } else {
                            Utils.getTranslatedValue(mContext.getString(R.string.weeks))
                        }
                        lastUsed = weeksCount.toString() + " " + weekStr + " " + Utils.getTranslatedValue(mContext.getString(R.string.ago))
                    }

                    (days < 365)-> {
                        val monthCount = days / 30
                        val monthStr = if (monthCount == 1) {
                            Utils.getTranslatedValue(mContext.getString(R.string.month))
                        } else {
                            Utils.getTranslatedValue(mContext.getString(R.string.months))
                        }
                        lastUsed = monthCount.toString() + " " + monthStr + " " + Utils.getTranslatedValue(mContext.getString(R.string.ago))
                    }

                    else -> {
                        val yearCount = days / 365
                        val yearStr = if (yearCount == 1) {
                            Utils.getTranslatedValue(mContext.getString(R.string.year))
                        } else {
                            Utils.getTranslatedValue(mContext.getString(R.string.years))
                        }
                        lastUsed = yearCount.toString() + " " + yearStr + " " + Utils.getTranslatedValue(mContext.getString(R.string.ago))
                    }
                }
                textView.text = lastUsed
            }
        }
    }
}

@BindingAdapter(value = ["term"], requireAll = true)
fun changeLanguageText(textView: TextView, term: String?) {
    textView.text = term?.let { Utils.getTranslatedValue(it) }
}

@BindingAdapter(value = ["searchTerm"], requireAll = true)
fun changeLanguageText(searchView: SearchView, searchTerm: String?) {
    searchView.isIconifiedByDefault = false
    searchView.queryHint = searchTerm?.let { Utils.getTranslatedValue(it) }
}

@BindingAdapter(value = ["hintString"], requireAll = true)
fun changeHintText(textInputLayout: TextInputLayout, hintString: String?) {
    textInputLayout.hint = hintString?.let { Utils.getTranslatedValue(it) }
}

@BindingAdapter(value = ["readMoreText"], requireAll = true)
fun setReadMoreText(textView: TextView, readMoreText: String?) {
    Handler(Looper.getMainLooper()).postDelayed({
        ShowMoreLess.Builder(textView.rootView.context)
            .textLengthAndLengthType(length = 2, textLengthType = ShowMoreLess.TYPE_LINE)
            .showMoreLabel(Utils.getTranslatedValue(textView.rootView.context.getString(R.string.read_more)))
            .showLessLabel(Utils.getTranslatedValue(textView.rootView.context.getString(R.string.read_less)))
            .showMoreLabelColor(ContextCompat.getColor(textView.rootView.context, R.color.colorPrimary))
            .showLessLabelColor(ContextCompat.getColor(textView.rootView.context, R.color.colorPrimary))
            .labelUnderLine(false).labelBold(false)
            .textClickable(textClickableInExpand = true, textClickableInCollapse = true).build()
            .apply {
                addShowMoreLess(textView = textView, text = readMoreText ?: "", isContentExpanded = false)
            }
    }, 100)
}

@BindingAdapter(value = ["dueAmount", "currencySymbol"], requireAll = true)
fun setDueAmount(textView: TextView, dueAmount: Float, currencySymbol: String) {
    if (dueAmount <= 0) {
        val actualAmount = if (dueAmount == 0f) {
            0
        } else {
            abs(dueAmount)
        }
        textView.text = "$currencySymbol$actualAmount"
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorGreen))
    } else {
        textView.text = "$currencySymbol$dueAmount"
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.colorRed))
    }
}