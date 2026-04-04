package com.example.cashcactus.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cashcactus.R
import com.example.cashcactus.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    onThemeChange: (Boolean) -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cash Cactus", style = MaterialTheme.typography.headlineSmall)
                    Text("Manage your money smartly")
                }

                HorizontalDivider()

                DrawerItem("Edit Profile", Icons.Default.Edit) {
                    navController.navigate("edit")
                }


                DrawerItem("About", Icons.Default.Info) {
                    navController.navigate("about")
                }

                DrawerItem("Help & Support", Icons.Default.Help) {
                    navController.navigate("help")
                }

                DrawerItem("Privacy Policy", Icons.Default.Lock) {
                    navController.navigate("privacy")
                }

                DrawerItem("Contact Us", Icons.Default.Call) {
                    navController.navigate("contact")
                }

                HorizontalDivider()

                DrawerItem("Dark Mode", Icons.Default.DarkMode) {
                    onThemeChange(true)
                }

                DrawerItem("Light Mode", Icons.Default.LightMode) {
                    onThemeChange(false)
                }

                DrawerItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                    viewModel.logout(navController) // ✅ FIXED
                }
            }
        }
    ) {

        // 🔥 IMPORTANT: FULL SCREEN BACKGROUND
        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.bg_finance),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            Scaffold(
                containerColor = Color.Transparent, // ✅ IMPORTANT

                topBar = {
                    TopAppBar(
                        title = { Text("Cash Cactus", color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, null, tint = Color.White)
                            }
                        }
                    )
                },

                bottomBar = {
                    NavigationBar(
                        containerColor = Color.Black.copy(alpha = 0.7f) // ✅ GLASS EFFECT
                    ) {

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("home") },
                            icon = { Icon(Icons.Default.Home, null) },
                            label = { Text("Home") }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("dashboard") },
                            icon = { Icon(Icons.Default.AccountBalance, null) },
                            label = { Text("Dashboard") }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("investment") },
                            icon = { Icon(Icons.Default.TrendingUp, null) },
                            label = { Text("Invest") }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("expenseGraph") },
                            icon = { Icon(Icons.Default.BarChart, null) },
                            label = { Text("Analysis") }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("vaultEntry") },
                            icon = { Icon(Icons.Default.Lock, null) },
                            label = { Text("Vault") }
                        )
                    }
                }

            ) { padding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text(
                                "Your Balance",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "₹${viewModel.monthlySalary.toInt()}",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text("Savings", color = Color.White)
                            Text("${viewModel.savingsPercent}%", color = Color.White)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Trend", color = Color.White)
                            Text(viewModel.trend, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text("Smart Tip", color = Color.White)

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Save at least 20% of your income every month",
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
        label = { Text(title) },
        selected = false,
        icon = { Icon(icon, contentDescription = null) },
        onClick = onClick
    )
}