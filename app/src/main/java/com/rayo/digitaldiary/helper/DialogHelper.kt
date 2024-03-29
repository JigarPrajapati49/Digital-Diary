package com.rayo.digitaldiary.helper

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rayo.digitaldiary.R
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DialogHelper @Inject constructor() {

    fun showMessageDialog(
        context: Context,
        message: String,
        positiveText : String,
        negativeText: String,
        yesListener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setNegativeButton(
                negativeText
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(positiveText, yesListener)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        with(dialog.getButton(DialogInterface.BUTTON_POSITIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }

    fun showOneButtonDialog(
        context: Context,
        message: String,
        okListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        }
    ) {
        val builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(Utils.getTranslatedValue(context.getString(R.string.ok)), okListener)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        with(dialog.getButton(DialogInterface.BUTTON_POSITIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }

    fun showInternetConnection(
        context: Context,
        message: String,
        okListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        }
    ) {
        val builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(Utils.getTranslatedValue(context.getString(R.string.retry)), okListener)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        with(dialog.getButton(DialogInterface.BUTTON_POSITIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }

    fun showPermissionRequiredDialog(
        context: Context,
        message: String,
        allowListener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setNegativeButton(Utils.getTranslatedValue(context.getString(R.string.cancel))) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(Utils.getTranslatedValue(context.getString(R.string.settings)), allowListener)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        with(dialog.getButton(DialogInterface.BUTTON_NEGATIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorRed)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
        with(dialog.getButton(DialogInterface.BUTTON_POSITIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }

    fun showTwoButtonDialog(
        context: Context,
        message: String,
        positiveListener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(context)
            .setMessage(message)
            .setNegativeButton(Utils.getTranslatedValue(context.getString(R.string.no))) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(Utils.getTranslatedValue(context.getString(R.string.yes)), positiveListener)

        val dialog: AlertDialog = builder.create()
        dialog.show()
        with(dialog.getButton(DialogInterface.BUTTON_NEGATIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
        with(dialog.getButton(DialogInterface.BUTTON_POSITIVE)) {
            setTextColor(
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setTypeface(Typeface.DEFAULT, Typeface.NORMAL)
        }
    }
    fun settingDialog(context: Context){
        MaterialAlertDialogBuilder(context)
        MaterialAlertDialogBuilder(context).create()
        MaterialAlertDialogBuilder(context).show()
    }

    fun showDialog(
        context: Context, message: String = "",
        positiveButtonText: String = "", positiveClickListener: DialogInterface.OnClickListener? = null,
        negativeButtonText: String = "", negativeClickListener: DialogInterface.OnClickListener? = null){
        val alertDialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(positiveButtonText,positiveClickListener)
        alertDialog.setNegativeButton(negativeButtonText,negativeClickListener)
        alertDialog.setCancelable(false)
        alertDialog.create().show()
    }
}