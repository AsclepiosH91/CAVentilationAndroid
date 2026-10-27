package com.caventilator.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caventilator.presentation.ui.AppDestination
import com.caventilator.presentation.ui.screens.department.DepartmentScreen
import com.caventilator.presentation.ui.screens.product.ProductScreen
import com.caventilator.presentation.ui.screens.ventilation.VentilationScreen
import com.caventilator.presentation.ui.theme.CAVentilatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CAVentilatorTheme {
                val navController = rememberNavController()
                var currentDestination by rememberSaveable {
                    mutableStateOf(AppDestination.Ventilation.route)
                }

                // NavigationSuiteScaffold adapte automatiquement la navigation :
                // BottomBar sur phone, NavigationRail sur tablet paysage, NavDrawer sur large tablet.
                NavigationSuiteScaffold(
                    navigationSuiteItems = {
                        AppDestination.all.forEach { dest ->
                            item(
                                selected = currentDestination == dest.route,
                                onClick = {
                                    currentDestination = dest.route
                                    navController.navigate(dest.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(dest.icon, contentDescription = stringResource(dest.labelRes)) },
                                label = { Text(stringResource(dest.labelRes)) }
                            )
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = AppDestination.Ventilation.route
                    ) {
                        composable(AppDestination.Products.route) { ProductScreen() }
                        composable(AppDestination.Departments.route) { DepartmentScreen() }
                        composable(AppDestination.Ventilation.route) { VentilationScreen() }
                    }
                }
            }
        }
    }
}
