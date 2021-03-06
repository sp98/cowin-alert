package com.santoshpillai.cowinalert.ui.createscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.santoshpillai.cowinalert.data.local.AlertDatabaseDao


class CreateAlertViewModelFactory(
    private val dataSource: AlertDatabaseDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateAlertViewModel::class.java)) {
            return CreateAlertViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class 3")
    }
}