package com.rayo.digitaldiary.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal
import com.rayo.digitaldiary.helper.Constants
import com.rayo.digitaldiary.helper.PreferenceManager
import com.rayo.digitaldiary.ui.OwnerMainActivity
import com.rayo.digitaldiary.ui.SplashActivity
import com.rayo.digitaldiary.ui.customer.CustomerData
import com.rayo.digitaldiary.ui.customer.CustomerMainActivity
import com.rayo.digitaldiary.ui.order.OrderData
import com.rayo.digitaldiary.ui.payment.PaymentData
import com.rayo.digitaldiary.ui.staff.StaffData
import org.json.JSONObject


class NotificationHelper(val context: Context, var preferenceManager: PreferenceManager) :
    OneSignal.OSNotificationOpenedHandler {


    override fun notificationOpened(result: OSNotificationOpenedResult) {
        val data: JSONObject? = result.notification.additionalData
        Log.e("TAG", "NotificationData: -----$data")


        val notificationType = data?.getString(Constants.NOTIFICATION_TYPE)
        Log.e("TAG", "NotificationType: -----$notificationType")

        val userType = preferenceManager.getPref(Constants.prefUserType, "")

        notificationType?.let { userType(notificationType, data, userType) }
    }

    private fun userType(type: String, data: JSONObject, userType: String) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(Constants.isFromNotification, true)

        if (preferenceManager.getPref(Constants.prefToken, "").isEmpty()) {
            intent.setClass(context, SplashActivity::class.java)
            context.startActivity(intent)
            return
        } else {
            if (userType == Constants.CUSTOMER) {
                intent.setClass(context, CustomerMainActivity::class.java)
            } else {
                intent.setClass(context, OwnerMainActivity::class.java)
            }
            notificationType(type, data, intent)
        }
    }

    private fun notificationType(type: String, data: JSONObject, intent: Intent) {
        when (type) {
            Constants.ORDER -> {
                var orderData: OrderData
                val customerData = CustomerData()
                val createdByStaffData = StaffData()
                val cancelledByStaffData = StaffData()
                data?.getJSONObject(Constants.ORDER)?.let { jsonObject1 ->
                    val parser = JsonParser()
                    val mJson: JsonElement = parser.parse(jsonObject1.toString())
                    orderData = Gson().fromJson(mJson, OrderData::class.java)
                    customerData.id = jsonObject1.getString("customer_id")
                    customerData.name = jsonObject1.getString("customer_name")
                    createdByStaffData.id = jsonObject1.getString("created_by_id")
                    createdByStaffData.name = jsonObject1.getString("created_by_name")
                    cancelledByStaffData.id = jsonObject1.getString("cancelled_by_id")
                    cancelledByStaffData.name = jsonObject1.getString("cancelled_by_name")
                    val bundle = Bundle()
                    bundle.putParcelable(Constants.orderData, orderData)
                    bundle.putParcelable(Constants.customerData, customerData)
                    bundle.putParcelable(Constants.createdByStaffData, createdByStaffData)
                    bundle.putParcelable(Constants.cancelledByStaffData, cancelledByStaffData)
                    bundle.putString(Constants.customerId, customerData.id)
                    bundle.putString(Constants.notificationType, Constants.ORDER)
                    bundle.putBoolean(Constants.isFromNotification,true)
                    intent.putExtras(bundle)
                    context.startActivity(intent)
                }
            }
            Constants.PAYMENT -> {
                var paymentData1: PaymentData
                val staffData = StaffData()
                val customerData = CustomerData()
                val payment = data.getJSONObject(Constants.PAYMENT)
                payment.let {
                    val parser = JsonParser()
                    val mJson: JsonElement = parser.parse(it.toString())
                    paymentData1 = Gson().fromJson(mJson, PaymentData::class.java)
                    val customerId = payment.getString("customer_id")
                    customerData.id = payment.getString("customer_id")
                    customerData.name = payment.getString("customer_name")
                    staffData.name = payment.getString("add_by_user_name")
                    staffData.id = payment.getString("add_by_user_id")
                    intent.putExtra(Constants.customerData, customerData)
                    intent.putExtra(Constants.createdByStaffData, staffData)
                    intent.putExtra(Constants.paymentData, paymentData1)
                    intent.putExtra(Constants.customerId, customerId)
                    intent.putExtra(Constants.notificationType, Constants.PAYMENT)
                    context.startActivity(intent)
                }
            }
        }
    }
}


