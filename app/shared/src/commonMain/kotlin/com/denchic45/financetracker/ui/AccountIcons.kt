package com.denchic45.financetracker.ui

import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.allDrawableResources

val accountIconsNames = setOf(
    // Cash
    "cash_banknote",
    "cash",
    "wallet",
    "coins",
    // Card
    "credit_card",
    "brand_mastercard",
    "building_bank",
    // Savings
    "pig_money",
    "moneybag"
)

val accountIcons = Res.allDrawableResources.filterKeys {
    it in accountIconsNames
}