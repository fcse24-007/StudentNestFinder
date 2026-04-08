package com.example.studentnestfinder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studentnestfinder.data.UserSession
import com.example.studentnestfinder.db.AppDatabase
import com.example.studentnestfinder.db.ChatRepository
import com.example.studentnestfinder.ui.auth.AuthScreen
import com.example.studentnestfinder.ui.auth.AuthViewModel
import com.example.studentnestfinder.ui.booking.BookingPaymentScreen
import com.example.studentnestfinder.ui.chat.ChatListScreen
import com.example.studentnestfinder.ui.chat.ChatScreen
import com.example.studentnestfinder.ui.home.HomeScreen
import com.example.studentnestfinder.ui.home.HomeViewModel
import com.example.studentnestfinder.ui.listingdetail.ListingDetailScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object ListingDetail : Screen("listing/{listingId}") {
        fun createRoute(listingId: Int) = "listing/$listingId"
    }
    object Booking : Screen("booking/{listingId}") {
        fun createRoute(listingId: Int) = "booking/$listingId"
    }
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{conversationId}/{recipientName}") {
        fun createRoute(conversationId: String, recipientName: String) = "chat/$conversationId/$recipientName"
    }
    object Preferences : Screen("preferences")
    object ProviderDashboard : Screen("provider_dashboard")
}

@Composable
fun AppNavigation(
    database: AppDatabase,
    isLoggedIn: Boolean
) {
    val navController = rememberNavController()
    val currentUser by UserSession.currentUser.collectAsState()
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Auth.route

    val listingDao = database.listingDao()
    val userDao = database.userDao()
    val chatRepository = ChatRepository(database.chatMessageDao())

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            val authViewModel = AuthViewModel(userDao, database)
            AuthScreen(
                viewModel = authViewModel,
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val homeViewModel = HomeViewModel(listingDao)
            HomeScreen(
                viewModel = homeViewModel,
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onProviderDashboardClick = {
                    navController.navigate(Screen.ProviderDashboard.route)
                }
            )
        }

        composable(
            route = Screen.ListingDetail.route,
            arguments = listOf(navArgument("listingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getInt("listingId") ?: return@composable
            ListingDetailScreen(
                listingId = listingId,
                listingDao = listingDao,
                userDao = userDao,
                onReserveClick = { id ->
                    navController.navigate(Screen.Booking.createRoute(id))
                },
                onChatClick = { providerId, providerName ->
                    val convId = chatRepository.conversationId(currentUser?.id ?: -1, providerId, listingId)
                    navController.navigate(Screen.Chat.createRoute(convId, providerName))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("listingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getInt("listingId") ?: return@composable
            val bookingViewModel = com.example.studentnestfinder.ui.booking.BookingViewModel(listingDao, database.reservationDao())
            
            BookingPaymentScreen(
                viewModel = bookingViewModel,
                listingId = listingId,
                studentId = currentUser?.id ?: -1,
                onPaymentComplete = { _ ->
                    navController.navigate(Screen.ChatList.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ChatList.route) {
            com.example.studentnestfinder.ui.chat.ChatListScreen(
                userId = currentUser?.id ?: -1,
                chatRepository = chatRepository,
                onChatSelect = { conversationId, recipientName ->
                    navController.navigate(Screen.Chat.createRoute(conversationId, recipientName))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("recipientName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val recipientName = backStackEntry.arguments?.getString("recipientName") ?: ""
            ChatScreen(
                conversationId = conversationId,
                recipientName = recipientName,
                currentUserId = currentUser?.id ?: -1,
                chatRepository = chatRepository,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Preferences.route) {
            com.example.studentnestfinder.ui.preferences.PreferencesScreen(
                userId = currentUser?.id ?: -1,
                preferenceDao = database.userPreferenceDao(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProviderDashboard.route) {
            com.example.studentnestfinder.ui.provider.ProviderDashboardScreen(
                providerId = currentUser?.id ?: -1,
                listingDao = listingDao,
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

