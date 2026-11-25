package com.example.navigationlab.navitgation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigationlab.Routes
import com.example.navigationlab.data.local.DatabaseProvider
import com.example.navigationlab.data.location.DefaultLocationRepository
import com.example.navigationlab.data.preferences.UserPreferencesRepository
import com.example.navigationlab.data.remote.RetrofitClient
import com.example.navigationlab.data.remote.StudentRemoteDataSource
import com.example.navigationlab.data.repository.DefaultStudentRepository
import com.example.navigationlab.data.repository.FavoriteRepository
import com.example.navigationlab.ui.detail.DetailViewModel
import com.example.navigationlab.ui.home.HomeViewModel
import com.example.navigationlab.ui.screen.AboutScreen
import com.example.navigationlab.ui.screen.DetailScreen
import com.example.navigationlab.ui.screen.HomeScreen
import com.example.navigationlab.ui.screen.ProfileScreen
import com.example.navigationlab.ui.favorites.FavoritesViewModel
import com.example.navigationlab.ui.location.LocationScreen
import com.example.navigationlab.ui.location.LocationViewModel
import com.example.navigationlab.ui.screen.FavoritesScreen
import com.google.android.gms.location.LocationServices

@Composable
fun NavigationLabApp(

) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val remoteDataSource = remember { StudentRemoteDataSource(RetrofitClient.apiService) }
    val repository = remember { DefaultStudentRepository(remoteDataSource) }
    val navController = rememberNavController()

    val preferencesRepository = remember {
        UserPreferencesRepository(context)
    }

    val favoriteRepository = remember {
        FavoriteRepository(
            DatabaseProvider.getDatabase(context)
                .favoriteStudentDao()
        )
    }

    val locationRepository = remember {
        val locationClient = LocationServices
            .getFusedLocationProviderClient(applicationContext)
        DefaultLocationRepository(
            context = applicationContext,
            client = locationClient
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel (
                factory = HomeViewModel.factory(repository, preferencesRepository)
            )
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = homeUiState,
                onStudentClick = { id ->
                    navController.navigate(Routes.detail(id))
                },
                onOpenProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onOpenAbout = {
                    navController.navigate(Routes.ABOUT)
                },
                onOpenFavorites = {
                    navController.navigate(Routes.FAVORITES)
                },
                onOpenLocation = {
                    navController.navigate(Routes.LOCATION)
                },
                onToggleSort = homeViewModel::toggleSortOrder,
                onRetry = homeViewModel::loadStudents,
                onSearch = homeViewModel::searchStudents,
                onSearchQueryChange = homeViewModel::onSearchQueryChange,
                onRefresh = homeViewModel::refreshStudents
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val studentId =
                backStackEntry.arguments?.getInt("studentId") ?: -1
            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.factory(repository, favoriteRepository)
            )

            val detailUiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(studentId) {
                viewModel.loadStudent(studentId)
            }

            DetailScreen(
                uiState = detailUiState,
                onBack = { navController.popBackStack() },
                onToggleFavorite = viewModel::toggleFavorite
            )
        }

        composable(
            route = Routes.FAVORITES
        ) {
            val favoritesViewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModel.factory(favoriteRepository)
            )
            val favoritesUiState by favoritesViewModel.uiState.collectAsStateWithLifecycle()

            FavoritesScreen(
                uiState = favoritesUiState,
                onStudentClick = { id ->
                    navController.navigate(Routes.detail(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable (
            route = Routes.ABOUT
        ) {
            AboutScreen (
                onBack = { navController.popBackStack() }
            )
        }

        composable (
            route = Routes.PROFILE
        ) {
            ProfileScreen (
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.LOCATION) {
            val locationViewModel: LocationViewModel = viewModel(
                factory = LocationViewModel.factory(locationRepository)
            )
            LocationScreen(
                viewModel = locationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}