/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ralphmueller.nycschools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ralphmueller.nycschools.ui.detail.Detail
import com.ralphmueller.nycschools.ui.detail.DetailViewModel
import com.ralphmueller.nycschools.ui.home.Home
import com.ralphmueller.nycschools.ui.home.HomeViewModel


@Composable
fun NavGraph(
    homeViewModel: HomeViewModel,
    detailViewModel: DetailViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Destinations.HOME) {
            val uiState by homeViewModel.uiState.collectAsState()
            Home(
                uiState = uiState,
                refreshSchools = homeViewModel::refreshSchools,
                sortingBy = homeViewModel::sortBy,
                navigateToDetails = NavigationActions(navController).navigateToDetails,
                onDismissError = homeViewModel::resetError
            )
        }
        composable(
            route = "${Destinations.DETAILS}/{dbn}",
            arguments = listOf(navArgument(name = "dbn") { type = NavType.StringType }
            )) {

            val uiState by detailViewModel.uiState.collectAsState()
            val dbn = it.arguments?.getString("dbn") ?: "no dbn!"

            Detail(
                uiState = uiState,
                loadSchoolData = detailViewModel::getSchool,
                dbn = dbn,
                popBackStack = navController::popBackStack
            )
        }
    }
}
