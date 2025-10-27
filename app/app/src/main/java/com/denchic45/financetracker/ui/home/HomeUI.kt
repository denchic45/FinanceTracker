package com.denchic45.financetracker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denchic45.financetracker.account.model.AccountType
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.ui.theme.FinanceTrackerTheme
import com.denchic45.financetracker.ui.transactions.ExpenseColor
import com.denchic45.financetracker.ui.transactions.IncomeColor
import com.denchic45.financetracker.ui.transactions.TransactionListItem
import com.denchic45.financetracker.ui.transactions.TransferColor
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Обзор финансов") })
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    GeneralBalanceCard(balance = state.generalBalance)
                }

                item {
                    MonthStatRow(stat = state.monthStats)
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Счета")
                        Spacer(Modifier.width(8.dp))
                        Text("Ваши счета", style = MaterialTheme.typography.titleLarge)
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(state.accounts) { account ->
                            AccountCard(account = account)
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Транзакции")
                        Spacer(Modifier.width(8.dp))
                        Text("Последние операции", style = MaterialTheme.typography.titleLarge)
                    }
                }

                // 6. Вертикальный список последних транзакций (TransactionItem)
                items(state.latestTransactions, key = { it.id }) { transaction ->
                    TransactionListItem(transaction = transaction)
                }
            }
        }
    }
}


@Composable
fun GeneralBalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Общий баланс:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                balance.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun MonthStatRow(stat: MonthStat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatBox(
            label = "Доходы",
            amountText = stat.income.toString(),
            color = IncomeColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatBox(
            label = "Расходы",
            amountText = stat.expense.toString(),
            color = ExpenseColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatBox(
            label = "Прибыль",
            amountText = stat.profit.toString(),
            color = TransferColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatBox(label: String, amountText: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                amountText,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AccountCard(account: AccountItem) {
    // Determine color based on the new AccountType
    val cardColor = when (account.type) {
        AccountType.CARD -> Color(0xFF673AB7).copy(alpha = 0.8f) // Primary color for Cards
        AccountType.CASH -> Color(0xFF8BC34A).copy(alpha = 0.8f) // Lighter green for Cash
        AccountType.BILL -> Color(0xFF00BCD4).copy(alpha = 0.8f) // Cyan for Bills
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                // Display the type's displayName next to the account name
                "${account.name} (${account.type.displayName})",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                account.displayedBalance,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(name = "1. General Balance Card")
@Composable
fun GeneralBalanceCardPreview() {
    FinanceTrackerTheme {
        GeneralBalanceCard(balance = 125000.50)
    }
}

// ---

@Preview(name = "2. Month Stat Row")
@Composable
fun MonthStatRowPreview() {
    val mockStat = MonthStat(expense = 35000.00, income = 60000.00, profit = 25000.00)
    FinanceTrackerTheme {
        MonthStatRow(stat = mockStat)
    }
}

// ---

@Preview(name = "3. Stat Box (Income)")
@Composable
fun StatBoxIncomePreview() {
    FinanceTrackerTheme {
        StatBox(
            label = "Доходы",
            amountText = "60 000 ₽",
            color = IncomeColor,
            modifier = Modifier.width(120.dp)
        )
    }
}

@Preview(name = "4. Account Card (Card)")
@Composable
fun AccountCardPreview() {
    val mockAccount = AccountItem(
        id = 1,
        name = "Основная",
        type = AccountType.CARD,
        balance = 9500000L // 95 000.00
    )
    FinanceTrackerTheme {
        AccountCard(account = mockAccount)
    }
}

// ---

@Preview(name = "5. Transaction List Item (Expense)")
@Composable
fun TransactionListItemExpensePreview() {
    val mockAccount = AccountItem(1, "СберБанк", AccountType.CARD, 9500000L)
    val mockCategory = CategoryItem(101, "Продукты", "", false)
    val mockTransaction = TransactionItem.Expense(
        id = 1,
        amount = 150000L, // 1500.00
        note = "Супермаркет 'Лента'",
        account = mockAccount,
        category = mockCategory,
    )

    FinanceTrackerTheme {
        Column(Modifier.padding(16.dp)) {
            TransactionListItem(transaction = mockTransaction)
        }
    }
}

@Preview(name = "6. Transaction List Item (Transfer)")
@Composable
fun TransactionListItemTransferPreview() {
    val cardAccount = AccountItem(1, "СберБанк", AccountType.CARD, 9500000L)
    val cashAccount = AccountItem(2, "Кошелек", AccountType.CASH, 500000L)
    val mockTransaction = TransactionItem.Transfer(
        id = 2,
        amount = 1000000L, // 10 000.00
        note = "Снятие наличных",
        account = cardAccount,
        incomeAccount = cashAccount
    )

    FinanceTrackerTheme {
        Column(Modifier.padding(16.dp)) {
            TransactionListItem(transaction = mockTransaction)
        }
    }
}