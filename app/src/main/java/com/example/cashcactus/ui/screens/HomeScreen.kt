package com.example.cashcactus.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.ui.components.BaseScreen
import com.example.cashcactus.ui.components.DashboardBackground
import com.example.cashcactus.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = DashboardBackground, drawerContentColor = MaterialTheme.colorScheme.onBackground) {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Cash Cactus", style = MaterialTheme.typography.headlineSmall)
                    Text("Manage your money smartly", style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DrawerItem("Edit Profile", Icons.Default.Edit) { navController.navigate("edit") }
                DrawerItem("About", Icons.Default.Info) { navController.navigate("about") }
                DrawerItem("Help & Support", Icons.Default.Help) { navController.navigate("help") }
                DrawerItem("Privacy Policy", Icons.Default.Lock) { navController.navigate("privacy") }
                DrawerItem("Contact Us", Icons.Default.Call) { navController.navigate("contact") }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DrawerItem("Settings", Icons.Default.Settings) {
                    navController.navigate("settings")
                }
                DrawerItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) { viewModel.logout(navController) }
            }
        }
    ) {
        BaseScreen(
            title = "Cash Cactus",
            navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null) }
            },
            bottomBar = {
                NavigationBar(containerColor = DashboardBackground) {
                    NavigationBarItem(selected = false, onClick = { navController.navigate("home") }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    NavigationBarItem(selected = false, onClick = { navController.navigate("dashboard") }, icon = { Icon(Icons.Default.AccountBalance, null) }, label = { Text("Dashboard") })
                    NavigationBarItem(selected = false, onClick = { navController.navigate("investment") }, icon = { Icon(Icons.Default.TrendingUp, null) }, label = { Text("Invest") })
                    NavigationBarItem(selected = false, onClick = { navController.navigate("expenseGraph") }, icon = { Icon(Icons.Default.BarChart, null) }, label = { Text("Analysis") })
                    NavigationBarItem(selected = false, onClick = { navController.navigate("vaultEntry") }, icon = { Icon(Icons.Default.Lock, null) }, label = { Text("Vault") })
                }
            }
        ) { contentPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.bg_finance),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth().animateContentSize()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Your Balance", style = MaterialTheme.typography.titleMedium)
                            Text("₹${viewModel.monthlySalary.toInt()}", style = MaterialTheme.typography.headlineLarge)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Savings"); Text("${viewModel.savingsPercent}%")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Trend"); Text(viewModel.trend)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Smart Tip")
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Save at least 20% of your income every month")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(title, style = MaterialTheme.typography.bodyLarge) },
        selected = false,
        icon = { Icon(icon, contentDescription = null) },
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
