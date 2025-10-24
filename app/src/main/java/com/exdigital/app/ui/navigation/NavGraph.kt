package com.exdigital.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.exdigital.app.ui.screens.CartScreen
import com.exdigital.app.ui.screens.HomeScreen
import com.exdigital.app.ui.screens.LoginScreen
import com.exdigital.app.ui.screens.ProductDetailScreen
import com.exdigital.app.ui.screens.RegisterScreen
import com.exdigital.app.ui.screens.SplashScreen
import com.exdigital.app.ui.screens.AdminScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Admin : Screen("admin")  // ⭐ NUEVA
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(navController, productId)
        }

        composable(Screen.Cart.route) {
            CartScreen(navController)
        }

        composable(Screen.Profile.route) {
            // ProfileScreen(navController)
            // TODO: Crear esta pantalla después
        }

        composable(Screen.Admin.route) {
            AdminScreen(navController)
        }
    }
}