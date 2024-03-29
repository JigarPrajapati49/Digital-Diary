package com.rayo.digitaldiary.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rayo.digitaldiary.ui.product.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM Product ORDER BY createdAt DESC")
    suspend fun getProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE active = 1")
    suspend fun getActiveProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(productList: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("SELECT COUNT(id) FROM Product WHERE active = 1")
    suspend fun getActiveProductCount(): Int

    @Query("DELETE FROM Product")
    suspend fun deleteTable()
}