package com.sparkingpathshala.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sparkingpathshala.app.data.repository.AppRepository

class ViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(AppRepository::class.java).newInstance(repository)
        } catch (e: Exception) {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}. ViewModels must have constructor taking AppRepository.")
        }
    }
}
