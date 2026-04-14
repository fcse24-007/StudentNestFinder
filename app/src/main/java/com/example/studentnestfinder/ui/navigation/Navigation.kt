package com.example.studentnestfinder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.studentnestfinder.ui.booking.StudentReservationsScreen
import com.example.studentnestfinder.ui.chat.ChatScreen
import com.example.studentnestfinder.ui.home.HomeScreen
import com.example.studentnestfinder.ui.home.HomeViewModel
import com.example.studentnestfinder.ui.info.FaqScreen
import com.example.studentnestfinder.ui.info.HelpScreen
import com.example.studentnestfinder.ui.listingdetail.ListingDetailScreen
import com.example.studentnestfinder.ui.theme.PrimaryColor
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
    object StudentReservations : Screen("student_reservations")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{conversationId}/{recipientName}") {
        fun createRoute(conversationId: String, recipientName: String) = "chat/$conversationId/$recipientName"
    }
    object Preferences : Screen("preferences")
    object ProviderDashboard : Screen("provider_dashboard")
    object AddListing : Screen("provider/listing/add")
    object EditListing : Screen("provider/listing/{listingId}/edit") {
        fun createRoute(listingId: Int) = "provider/listing/$listingId/edit"
    }
    object Help : Screen("help")
    object Faq : Screen("faq")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    database: AppDatabase,
    isLoggedIn: Boolean
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser by UserSession.currentUser.collectAsState()
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Auth.route

    val listingDao = database.listingDao()
    val listingImageDao = database.listingImageDao()
    val userDao = database.userDao()
    val reservationDao = database.reservationDao()
    val chatRepository = ChatRepository(database.chatMessageDao())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Surface(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium),
                    color = PrimaryColor
                ) {
                    Text(
                        text = "SNF",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Messages") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.ChatList.route)
                    }
                )
                if (currentUser?.role == "STUDENT") {
                    NavigationDrawerItem(
                        label = { Text("My Reservations") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.StudentReservations.route)
                        }
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Preferences.route)
                    }
                )
                if (currentUser?.role == "PROVIDER") {
                    NavigationDrawerItem(
                        label = { Text("Landlord Dashboard") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.ProviderDashboard.route)
                        }
                    )
                }
                NavigationDrawerItem(
                    label = { Text("Help") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Help.route)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("FAQ") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Faq.route)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        UserSession.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            AuthScreen(
                viewModel = authViewModel,
                onNavigateHome = { role ->
                    val target = if (role == "PROVIDER") Screen.ProviderDashboard.route else Screen.Home.route
                    navController.navigate(target) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                return@composable
            }
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                isProvider = currentUser?.role == "PROVIDER",
                viewModel = homeViewModel,
                listingImageDao = listingImageDao,
                onListingClick = { listingId ->
                    navController.navigate(Screen.ListingDetail.createRoute(listingId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onProviderDashboardClick = {
                    navController.navigate(Screen.ProviderDashboard.route)
                },
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onHelpClick = {
                    navController.navigate(Screen.Help.route)
                },
                onFaqClick = {
                    navController.navigate(Screen.Faq.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
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
                currentUserId = currentUser?.id ?: -1,
                isProvider = currentUser?.role == "PROVIDER",
                listingDao = listingDao,
                listingImageDao = listingImageDao,
                reservationDao = reservationDao,
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
                },
                onSettingsClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onHelpClick = {
                    navController.navigate(Screen.Help.route)
                },
                onFaqClick = {
                    navController.navigate(Screen.Faq.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("listingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getInt("listingId") ?: return@composable
            val bookingViewModel: com.example.studentnestfinder.ui.booking.BookingViewModel = hiltViewModel()
            
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
                },
                onSettingsClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onHelpClick = {
                    navController.navigate(Screen.Help.route)
                },
                onFaqClick = {
                    navController.navigate(Screen.Faq.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.StudentReservations.route) {
            StudentReservationsScreen(
                studentId = currentUser?.id ?: -1,
                reservationDao = reservationDao,
                onOpenChat = { providerId, providerName, listingId ->
                    val convId = chatRepository.conversationId(currentUser?.id ?: -1, providerId, listingId)
                    navController.navigate(Screen.Chat.createRoute(convId, providerName))
                },
                onBack = {
                    navController.popBackStack()
                },
                onSettingsClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onHelpClick = {
                    navController.navigate(Screen.Help.route)
                },
                onFaqClick = {
                    navController.navigate(Screen.Faq.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ChatList.route) {
            com.example.studentnestfinder.ui.chat.ChatListScreen(
                userId = currentUser?.id ?: -1,
                chatRepository = chatRepository,
                userDao = userDao,
                onChatSelect = { conversationId, recipientName ->
                    navController.navigate(Screen.Chat.createRoute(conversationId, recipientName))
                },
                onBack = {
                    navController.popBackStack()
                },
                onSettingsClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onHelpClick = {
                    navController.navigate(Screen.Help.route)
                },
                onFaqClick = {
                    navController.navigate(Screen.Faq.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
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
                },
                onSettingsClick = {
                    navController.navigate(Screen.Preferences.route)
                },
                onHelpClick = {
                    navController.navigate(Screen.Help.route)
                },
                onFaqClick = {
                    navController.navigate(Screen.Faq.route)
                },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Preferences.route) {
            com.example.studentnestfinder.ui.preferences.PreferencesScreen(
                userId = currentUser?.id ?: -1,
                preferenceDao = database.userPreferenceDao(),
                onBack = { navController.popBackStack() },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onFaqClick = { navController.navigate(Screen.Faq.route) },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProviderDashboard.route) {
            com.example.studentnestfinder.ui.provider.ProviderDashboardScreen(
                providerId = currentUser?.id ?: -1,
                listingDao = listingDao,
                reservationDao = reservationDao,
                onListingClick = { listingId ->
                    navController.navigate(Screen.EditListing.createRoute(listingId))
                },
                onBack = { navController.popBackStack() },
                onAddListing = { navController.navigate(Screen.AddListing.route) },
                onSettingsClick = { navController.navigate(Screen.Preferences.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onFaqClick = { navController.navigate(Screen.Faq.route) },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddListing.route) {
            com.example.studentnestfinder.ui.provider.AddListingScreen(
                providerId = currentUser?.id ?: -1,
                listingDao = listingDao,
                listingImageDao = listingImageDao,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Preferences.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onFaqClick = { navController.navigate(Screen.Faq.route) },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EditListing.route,
            arguments = listOf(navArgument("listingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val listingId = backStackEntry.arguments?.getInt("listingId") ?: return@composable
            com.example.studentnestfinder.ui.provider.AddListingScreen(
                providerId = currentUser?.id ?: -1,
                listingDao = listingDao,
                listingImageDao = listingImageDao,
                listingId = listingId,
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(Screen.Preferences.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onFaqClick = { navController.navigate(Screen.Faq.route) },
                onLogout = {
                    UserSession.logout()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(
                onBack = { navController.popBackStack() },
                onFaqClick = { navController.navigate(Screen.Faq.route) }
            )
        }

        composable(Screen.Faq.route) {
            FaqScreen(
                onBack = { navController.popBackStack() },
                onHelpClick = { navController.navigate(Screen.Help.route) }
            )
        }
    }
    }
}
