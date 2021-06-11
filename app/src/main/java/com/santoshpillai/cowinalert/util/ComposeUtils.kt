package com.santoshpillai.cowinalert.util

import android.content.Context
import android.widget.Toast

fun showToastMsg(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

}