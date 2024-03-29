package com.rayo.digitaldiary.ui.product

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.*
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.annotations.SerializedName
import com.rayo.digitaldiary.api.CommonResponse
import com.rayo.digitaldiary.database.TimestampConverter

class AddProductResponse : CommonResponse() {
    @SerializedName("data")
    val productData: Product? = null
}

@Entity(tableName = "Product", indices = [Index(value = ["createdAt"])])
open class Product() : BaseObservable(), Parcelable {

    /* Created new object because of two way dataBinding changing value
      in listing without submitting data */
    fun copy(product: Product): Product {
        return Product().apply {
            id = product.id
            name = product.name
            weight = product.weight
            price = product.price
            unit = product.unit
            active = product.active
            isProductSelected = product.isProductSelected
            quantity = product.quantity
            createdAt = product.createdAt
            updatedAt = product.updatedAt
        }
    }

    fun update(oldProductData: Product, newProductData: Product): Product {
        return oldProductData.apply {
            id = newProductData.id
            name = newProductData.name
            weight = newProductData.weight
            price = newProductData.price
            unit = newProductData.unit
            active = newProductData.active
            isProductSelected = newProductData.isProductSelected
            quantity = newProductData.quantity
            createdAt = newProductData.createdAt
            updatedAt = newProductData.updatedAt
        }
    }

    @PrimaryKey
    @SerializedName("_id")
    var id: String = ""

    @get:Bindable
    @SerializedName("name")
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    @SerializedName("weight")
    var weight: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.weight)
        }

    @get:Bindable
    @SerializedName("price")
    var price: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.price)
        }

    @SerializedName("unit")
    var unit: String = ""

    @ColumnInfo("active")
    @SerializedName("is_active")
    var active: Int = 1

    @Transient
    @Ignore
    var isProductSelected = 0

    @Ignore
    @get:Bindable
    var quantity: String = "1"
        set(value) {
            field = value
            notifyPropertyChanged(BR.quantity)
        }

    @TypeConverters(TimestampConverter::class)
    var createdAt: String = ""

    var updatedAt: String = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        weight = parcel.readString().toString()
        price = parcel.readString().toString()
        unit = parcel.readString().toString()
        active = parcel.readInt()
        isProductSelected = parcel.readInt()
        quantity = parcel.readString().toString()
        createdAt = parcel.readString().toString()
        updatedAt = parcel.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(weight)
        parcel.writeString(price)
        parcel.writeString(unit)
        parcel.writeInt(active)
        parcel.writeInt(isProductSelected)
        parcel.writeString(quantity)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}


class ProductJsonExclusion : ExclusionStrategy {
    override fun shouldSkipClass(arg0: Class<*>?): Boolean {
        return false
    }

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.declaringClass == Product::class.java && f.name == "active"
                || f.declaringClass == Product::class.java && f.name == "createdAt"
                || f.declaringClass == Product::class.java && f.name == "updatedAt"
    }
}
