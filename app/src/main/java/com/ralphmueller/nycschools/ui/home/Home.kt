package com.ralphmueller.nycschools.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ralphmueller.nycschools.model.School
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Home(
    uiState: HomeUiState,
    refreshSchools: (Boolean) -> Unit,
    onDismissError: () -> Unit,
    navigateToDetails: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        refreshSchools(true)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text("NYC School Data") }) },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(Modifier.pullRefresh(state)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
            ) {
                if (!refreshing) {
                    items(uiState.schools) { school ->
                        ListItem(
                            school = school,
                            onClick = { navigateToDetails(school.dbn) }
                        )
                    }
                }
            }
            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        }
    }
    if (uiState.exception != null) {

        val errorMessageText = remember(uiState) { uiState.exception }

        LaunchedEffect(errorMessageText, scaffoldState.snackbarHostState) {

            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText.toString(),
                actionLabel = "OK",
                duration = SnackbarDuration.Indefinite
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onDismissError()
            }
        }
    }
}

@Composable
fun ListItem(
    school: School,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(IntrinsicSize.Max)
            .selectable(selected = true, onClick = onClick)
            .padding(10.dp)
    ) {
        Text(text = school.school_name)
        Text(text = school.location)
        Text(text = school.website)

    }
}

@Preview
@Composable
fun PreviewHome(
    uiState: HomeUiState = HomeUiState(schools = listOf(school, school))
) {
    Home(uiState = uiState, {}, {}, {})
}

val school = School(
    dbn = "12345",
    school_name = "Clinton School Writers & Artists, M.S. 260",
    overview_paragraph = "Students who are prepared for college must have an education that encourages them to take risks as they produce and perform. Our college preparatory curriculum develops writers and has built a tight-knit community. Our school develops students who can think analytically and write creatively. Our arts programming builds on our 25 years of experience in visual, performing arts and music on a middle school level. We partner with New Audience and the Whitney Museum as cultural partners. We are a International Baccalaureate (IB) candidate school that offers opportunities to take college courses at neighboring universities.",
    location = "10 East 15th Street, Manhattan NY 10003 (40.736526, -73.992727)",
    phone_number = "212-524-4360",
    school_email = "admissions@theclintonschool.net",
    website = "www.theclintonschool.net",
    num_of_sat_test_takers = "",
    sat_critical_reading_avg_score = "",
    sat_math_avg_score = "",
    sat_writing_avg_score = ""
)