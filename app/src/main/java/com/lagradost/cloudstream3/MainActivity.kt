package com.lagradost.cloudstream3

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.lagradost.cloudstream3.data.local.AppDatabase
import com.lagradost.cloudstream3.data.repository.AppRepository
import com.lagradost.cloudstream3.ui.Screen
import com.lagradost.cloudstream3.ui.home.HomeViewModel
import com.lagradost.cloudstream3.ui.home.HomeViewModelFactory
import com.lagradost.cloudstream3.ui.search.SearchViewModel
import com.lagradost.cloudstream3.ui.theme.CloudStreamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudStreamTheme {
                MainScreen()
            }
        }
    }
}

sealed class BottomNavItem(val route: Any, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(Screen.Home, Icons.Default.Home, "Home")
    data object Search : BottomNavItem(Screen.Search, Icons.Default.Search, "Search")
    data object Library : BottomNavItem(Screen.Library, Icons.Default.LibraryBooks, "Library")
    data object Downloads : BottomNavItem(Screen.Downloads, Icons.Default.Download, "Downloads")
    data object Settings : BottomNavItem(Screen.Settings, Icons.Default.Settings, "Settings")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Library,
        BottomNavItem.Downloads,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { 
                        it.route?.contains(item.route::class.simpleName ?: "") == true 
                    } == true
                    
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> { HomeScreen(navController) }
            composable<Screen.Search> { SearchScreen(navController) }
            composable<Screen.Library> { LibraryScreen(navController) }
            composable<Screen.Downloads> { DownloadsScreen(navController) }
            composable<Screen.Settings> { SettingsScreen(navController) }
            composable<Screen.Result> { backStackEntry ->
                val result: Screen.Result = backStackEntry.toRoute()
                ResultScreen(navController, result.url, result.apiName)
            }
            composable<Screen.Player> { backStackEntry ->
                val player: Screen.Player = backStackEntry.toRoute()
                PlayerScreen(navController, player.url, player.title)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { AppRepository(database.continueWatchingDao()) }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
    
    val continueWatching by viewModel.continueWatching.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Continue Watching",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        if (continueWatching.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No items yet")
                    Button(onClick = { viewModel.addDummyItem() }) {
                        Text("Add Demo Item")
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(continueWatching) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            navController.navigate(Screen.Player(item.url, item.title))
                        }
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Column {
                                Text(item.title, style = MaterialTheme.typography.titleLarge)
                                Text("Progress: ${item.progress}/${item.duration}", style = MaterialTheme.typography.bodyMedium)
                                Text("Source: ${item.apiName}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(navController: androidx.navigation.NavController) {
    val viewModel: SearchViewModel = viewModel()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = { 
                query = it
                viewModel.search(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search...") },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(searchResults) { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        navController.navigate(Screen.Result(result.url, result.apiName))
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Column {
                            Text(result.name, style = MaterialTheme.typography.titleLarge)
                            Text("Source: ${result.apiName}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(navController: androidx.navigation.NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Library Screen - Feature in progress")
    }
}

@Composable
fun DownloadsScreen(navController: androidx.navigation.NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Downloads Screen - Feature in progress")
    }
}

@Composable
fun SettingsScreen(navController: androidx.navigation.NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings Screen - Feature in progress")
    }
}

@Composable
fun ResultScreen(navController: androidx.navigation.NavController, url: String, apiName: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Result Screen for $url from $apiName")
    }
}

@Composable
fun PlayerScreen(navController: androidx.navigation.NavController, url: String, title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Player Screen for $title at $url")
    }
}
