package com.denchic45.financetracker.ui.transactiondetails

import androidx.lifecycle.ViewModel
import com.denchic45.financetracker.data.repository.TransactionRepository

class TransactionDetailsViewModel(
    private val transactionId: Long,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

}