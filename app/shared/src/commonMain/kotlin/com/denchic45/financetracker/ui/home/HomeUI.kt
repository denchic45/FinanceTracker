package com.denchic45.financetracker.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denchic45.financetracker.api.account.model.AccountType
import com.denchic45.financetracker.api.statistic.model.TotalsAmount
import com.denchic45.financetracker.domain.model.AccountItem
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.domain.model.TransactionItem
import com.denchic45.financetracker.domain.model.displayedGeneralBalance
import com.denchic45.financetracker.ui.HeaderItem
import com.denchic45.financetracker.ui.MainTopAppBar
import com.denchic45.financetracker.ui.NoDataContent
import com.denchic45.financetracker.ui.SmallTextButton
import com.denchic45.financetracker.ui.TransactionListItem
import com.denchic45.financetracker.ui.resource.isLoading
import com.denchic45.financetracker.ui.resource.onData
import com.denchic45.financetracker.ui.resource.onSuccess
import com.denchic45.financetracker.ui.theme.FinanceTrackerTheme
import com.denchic45.financetracker.ui.transactions.ExpenseColor
import com.denchic45.financetracker.ui.transactions.IncomeColor
import com.denchic45.financetracker.ui.transactions.TransferColor
import com.denchic45.financetracker.ui.util.convertToCurrency
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.account_add
import financetracker_app.shared.generated.resources.accounts_title
import financetracker_app.shared.generated.resources.add
import financetracker_app.shared.generated.resources.common_all
import financetracker_app.shared.generated.resources.home_general_balance
import financetracker_app.shared.generated.resources.txn_expense
import financetracker_app.shared.generated.resources.txn_income
import financetracker_app.shared.generated.resources.txn_list_empty
import financetracker_app.shared.generated.resources.txn_profit
import financetracker_app.shared.generated.resources.txns_title
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigationDrawerClick: () -> Unit
) {
    val monthStatistics by viewModel.monthStatistics.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val latestTransactions by viewModel.latestTransactions.collectAsState()

    Scaffold(
        topBar = {
            MainTopAppBar(
                onNavigationIconClick = onNavigationDrawerClick,
                actions = {}
            )
        }) { paddingValues ->
        if (monthStatistics.isLoading() || accounts.isLoading() || latestTransactions.isLoading()) {
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
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    accounts.onData { failure, items ->
                        GeneralBalanceCard(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            displayedBalance = items.displayedGeneralBalance
                        )
                    }
                }

                monthStatistics.onSuccess {
                    item {
                        MonthStatRow(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            totalsAmount = it
                        )
                    }
                }

                item { HeaderItem(stringResource(Res.string.accounts_title)) }

                accounts.onData { failure, accounts ->
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(accounts) { account ->
                                AccountCard(
                                    account = account,
                                    onClick = { viewModel.onAccountClick(account.id) })
                            }

                            item {
                                OutlinedCard(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(100.dp)
                                        .clickable(onClick = viewModel::onCreateAccountClick),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(painterResource(Res.drawable.add), null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(Res.string.account_add))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                item {
                    HeaderItem(
                        name = stringResource(Res.string.txns_title),
                        action = {
                            SmallTextButton(onClick = viewModel::onShowMoreTransactionsClick) {
                                Text(stringResource(Res.string.common_all))
                            }
                        })
                }

                latestTransactions.onData { failure, latestTransactions ->
                    if (latestTransactions.isNotEmpty()) {
                        items(latestTransactions, key = { it.id }) { transaction ->
                            TransactionListItem(
                                transaction = transaction,
                                onClick = { viewModel.onTransactionClick(transaction.id) })
                        }
                    } else {
                        item {
                            NoDataContent(
                                title = {
                                    Text(stringResource(Res.string.txn_list_empty))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GeneralBalanceCard(modifier: Modifier = Modifier, displayedBalance: String) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                stringResource(Res.string.home_general_balance),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                displayedBalance,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun MonthStatRow(modifier: Modifier = Modifier, totalsAmount: TotalsAmount) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        StatBox(
            label = stringResource(Res.string.txn_income),
            amountText = totalsAmount.incomes.convertToCurrency(),
            color = IncomeColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatBox(
            label = stringResource(Res.string.txn_expense),
            amountText = totalsAmount.expenses.convertToCurrency(),
            color = ExpenseColor,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        StatBox(
            label = stringResource(Res.string.txn_profit),
            amountText = totalsAmount.profit.convertToCurrency(),
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
fun AccountCard(account: AccountItem, onClick: () -> Unit) {
    val cardColor = when (account.type) {
        AccountType.ORDINARY -> Color(0xFF673AB7).copy(alpha = 0.8f) // Primary color for Cards
        AccountType.DEBT -> Color(0xFF8BC34A).copy(alpha = 0.8f) // Lighter green for Cash
        AccountType.SAVINGS -> Color(0xFF00BCD4).copy(alpha = 0.8f) // Cyan for Bills
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clickable(onClick = onClick),
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
                account.name,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                account.displayedBalance,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview(name = "1. General Balance Card")
@Composable
fun GeneralBalanceCardPreview() {
    FinanceTrackerTheme {
        GeneralBalanceCard(displayedBalance = "125000")
    }
}

// ---

@Preview(name = "2. Month Stat Row")
@Composable
fun MonthStatRowPreview() {
    val mockStat = TotalsAmount(expenses = 35000, incomes = 60000, profit = 25000)
    FinanceTrackerTheme {
        MonthStatRow(totalsAmount = mockStat)
    }
}

// ---

@Preview(name = "3. Stat Box (Income)")
@Composable
fun StatBoxIncomePreview() {
    FinanceTrackerTheme {
        StatBox(
            label = stringResource(Res.string.txn_income),
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
        id = UUID.randomUUID(),
        name = "Основная",
        type = AccountType.ORDINARY,
        balance = 9500000L // 95 000.00
    )
    FinanceTrackerTheme {
        AccountCard(
            account = mockAccount, onClick = {})
    }
}

// ---

@OptIn(ExperimentalTime::class)
@Preview(name = "5. Transaction List Item (Expense)")
@Composable
fun TransactionListItemExpensePreview() {
    val mockAccount = AccountItem(UUID.randomUUID(), "СберБанк", AccountType.ORDINARY, 9500000L)
    val mockCategory = CategoryItem(101, "Продукты", "", false)
    val mockTransaction = TransactionItem.Expense(
        id = 1,
        amount = 150000L, // 1500.00
        datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        note = "Супермаркет 'Лента'",
        account = mockAccount,
        category = mockCategory,
        tags = emptyList()
    )

    FinanceTrackerTheme {
        Column(Modifier.padding(16.dp)) {
            TransactionListItem(transaction = mockTransaction, onClick = {})
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(name = "6. Transaction List Item (Transfer)")
@Composable
fun TransactionListItemTransferPreview() {
    val cardAccount = AccountItem(UUID.randomUUID(), "СберБанк", AccountType.DEBT, 9500000L)
    val cashAccount = AccountItem(UUID.randomUUID(), "Кошелек", AccountType.ORDINARY, 500000L)
    val mockTransaction = TransactionItem.Transfer(
        id = 2,
        amount = 1000000L, // 10 000.00
        datetime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        note = "Снятие наличных",
        account = cardAccount,
        incomeAccount = cashAccount
    )

    FinanceTrackerTheme {
        Column(Modifier.padding(16.dp)) {
            TransactionListItem(
                transaction = mockTransaction, onClick = {})
        }
    }
}