package com.santoshpillai.cowinalert.ui.alertscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.santoshpillai.cowinalert.data.local.AlertDatabaseDao


class AlertViewModelFactory(
    private val dataSource: AlertDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
            return AlertViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class 2")
    }
}