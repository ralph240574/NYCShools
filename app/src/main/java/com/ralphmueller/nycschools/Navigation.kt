package com.ralphmueller.nycschools

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Destinations {
    const val HOME = "home"
    const val DETAILS = "details"
}

/**
 * Models the navigation actions in the app.
 */
class NavigationActions(navController: NavHostController) {

    val navigateToDetails: (String) -> Unit = {
        println("${Destinations.DETAILS}/${it}")
        navController.navigate(route = "${Destinations.DETAILS}/${it}") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
