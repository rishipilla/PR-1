package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DatasetsScreen
import com.example.ui.screens.EvidenceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrayogaBgGradientEnd
import com.example.ui.theme.PrayogaBgWhite
import com.example.ui.theme.PrayogaCyan
import com.example.ui.theme.PrayogaCyanDark
import com.example.ui.theme.PrayogaGlassBorder
import com.example.ui.theme.PrayogaGlassWhite
import com.example.ui.theme.PrayogaTextMuted
import com.example.ui.theme.PrayogaTextPrimary
import com.example.viewmodel.PrayogaViewModel

enum class PrayogaNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    SCANNER("Scan", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner),
    DATASETS("Datasets", Icons.Filled.Dataset, Icons.Outlined.Dataset),
    EVIDENCE("Pipeline", Icons.Filled.Security, Icons.Outlined.Security),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PrayogaApp()
            }
        }
    }
}

@Composable
fun PrayogaApp() {
    val viewModel: PrayogaViewModel = viewModel()
    var currentTab by remember { mutableStateOf(PrayogaNavTab.HOME) }

    // System Back Handler
    BackHandler(enabled = currentTab != PrayogaNavTab.HOME) {
        currentTab = PrayogaNavTab.HOME
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrayogaBgWhite,
                        PrayogaBgGradientEnd
                    )
                )
            ),
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = PrayogaGlassWhite,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, PrayogaGlassBorder)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    PrayogaNavTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrayogaCyanDark,
                                selectedTextColor = PrayogaCyanDark,
                                unselectedIconColor = PrayogaTextMuted,
                                unselectedTextColor = PrayogaTextMuted,
                                indicatorColor = Color(0xFFE0F2FE)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    PrayogaNavTab.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToScanner = { currentTab = PrayogaNavTab.SCANNER },
                            onNavigateToDatasets = { currentTab = PrayogaNavTab.DATASETS }
                        )
                    }
                    PrayogaNavTab.SCANNER -> {
                        ScannerScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentTab = PrayogaNavTab.HOME }
                        )
                    }
                    PrayogaNavTab.DATASETS -> {
                        DatasetsScreen(viewModel = viewModel)
                    }
                    PrayogaNavTab.EVIDENCE -> {
                        EvidenceScreen(viewModel = viewModel)
                    }
                    PrayogaNavTab.PROFILE -> {
                        ProfileSettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
