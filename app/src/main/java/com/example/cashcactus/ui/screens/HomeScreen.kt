package com.example.cashcactus.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
            ModalDrawerSheet(
                drawerContainerColor = DashboardBackground,
                drawerContentColor = MaterialTheme.colorScheme.onBackground
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.home_tagline), style = MaterialTheme.typography.bodyMedium)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerItem(stringResource(R.string.edit_profile), Icons.Default.Edit) { navController.navigate("edit") }
                DrawerItem(stringResource(R.string.about), Icons.Default.Info) { navController.navigate("about") }
                DrawerItem(stringResource(R.string.help_support), Icons.Default.Help) { navController.navigate("help") }
                DrawerItem(stringResource(R.string.privacy_policy), Icons.Default.Lock) { navController.navigate("privacy") }
                DrawerItem(stringResource(R.string.contact_us), Icons.Default.Call) { navController.navigate("contact") }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerItem(stringResource(R.string.settings), Icons.Default.Settings) {
                    navController.navigate("settings")
                }

                DrawerItem(stringResource(R.string.logout), Icons.AutoMirrored.Filled.ExitToApp) {
                    viewModel.logout(navController)
                }
            }
        }
    ) {
        BaseScreen(
            title = stringResource(R.string.app_name),
            navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, null)
                }
            },
            bottomBar = {
                NavigationBar(containerColor = DashboardBackground) {
                    NavigationBarItem(false, { navController.navigate("home") }, { Icon(Icons.Default.Home, null) }, label = { Text(stringResource(R.string.home)) })
                    NavigationBarItem(false, { navController.navigate("dashboard") }, { Icon(Icons.Default.AccountBalance, null) }, label = { Text(stringResource(R.string.dashboard)) })
                    NavigationBarItem(false, { navController.navigate("investment") }, { Icon(Icons.Default.TrendingUp, null) }, label = { Text(stringResource(R.string.nav_invest)) })
                    NavigationBarItem(false, { navController.navigate("expenseGraph") }, { Icon(Icons.Default.BarChart, null) }, label = { Text(stringResource(R.string.nav_analysis)) })
                    NavigationBarItem(false, { navController.navigate("vaultEntry") }, { Icon(Icons.Default.Lock, null) }, label = { Text(stringResource(R.string.vault)) })
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                ) {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text(
                                stringResource(R.string.your_balance),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "₹${viewModel.monthlySalary.toInt()}",
                                style = MaterialTheme.typography.headlineLarge
                            )

                            // ✅ ADDED TEXT HERE
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Money grows well when managed well",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
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