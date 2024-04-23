package org.d3if3141.miniproject.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("mainscreen")
    data object About : Screen("aboutscreen")
}
