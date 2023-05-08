package com.ralphmueller.nycschools.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ralphmueller.nycschools.model.School
import kotlinx.coroutines.launch

const val SCHOOL_LIST = "SchoolList"

@OptIn(ExperimentalMaterialApi::class)

@Composable
fun Home(
    uiState: HomeUiState,
    refreshSchools: (Boolean) -> Unit,
    sortingBy: (SortingOption) -> Unit,
    onDismissError: () -> Unit,
    navigateToDetails: (String) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(true) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        refreshSchools(true)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    val openSortDialog = remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("NYC School Data") },
                actions = {
                    IconButton(onClick = { openSortDialog.value = true }) {
                        Icon(Icons.Filled.Sort, "Sort")
                    }
                })
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Box(Modifier.pullRefresh(state)) {
            if (openSortDialog.value) {
                AlertDialog(
                    onDismissRequest = { openSortDialog.value = false },
                    title = { Text(text = "Sort By") },
                    text = {
                        SortingOptions(
                            sortingBy = sortingBy,
                            lastSorting = uiState.sortedBy
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            openSortDialog.value = false
                            sortingBy(uiState.sortedBy)
                        }) {
                            Text("OK")
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
                    .testTag(SCHOOL_LIST)
            ) {
                items(uiState.schools) { school ->
                    ListItem(
                        school = school,
                        onClick = { navigateToDetails(school.dbn) }
                    )
                    refreshing = false
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
        Text(
            text = school.school_name, fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = school.location)
    }
}

@Preview
@Composable
fun PreviewHome(
    uiState: HomeUiState = HomeUiState(
        schools = listOf(school, school),
        sortedBy = SortingOption.MATH_SCORE
    )
) {
    Home(uiState = uiState, {}, {}, {}, {})
}

@Composable
fun SortingOptions(sortingBy: (SortingOption) -> Unit, lastSorting: SortingOption) {
    val radioOptions = listOf(
        SortingOption.READING_SCORE,
        SortingOption.WRITING_SCORE,
        SortingOption.MATH_SCORE
    )
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(lastSorting) }
    Column {
        radioOptions.forEach { sortingBy ->
            Row(Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (sortingBy == selectedOption),
                    onClick = {
                        onOptionSelected(sortingBy)
                        sortingBy(sortingBy)
                    }
                )
                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (sortingBy == selectedOption),
                    onClick = {
                        onOptionSelected(sortingBy)
                        sortingBy(sortingBy)
                    }
                )
                Text(
                    text = sortingBy.toString()
                )
            }
        }
    }
}

val school = School(
    dbn = "12345",
    school_name = "Clinton School Writers & Artists, M.S. 260",
    overview_paragraph = "Students who are prepared for college must have an education that encourages them to take risks as they produce and perform. Our college preparatory curriculum develops writers and has built a tight-knit community. Our school develops students who can think analytically and write creatively. Our arts programming builds on our 25 years of experience in visual, performing arts and music on a middle school level. We partner with New Audience and the Whitney Museum as cultural partners. We are a International Baccalaureate (IB) candidate school that offers opportunities to take college courses at neighboring universities.",
    location = "10 East 15th Street, Manhattan NY 10003 (40.736526, -73.992727)",
    phone_number = "212-524-4360",
    school_email = "admissions@theclintonschool.net",
    website = "www.theclintonschool.net",
    num_of_sat_test_takers = "123",
    sat_critical_reading_avg_score = "300",
    sat_math_avg_score = "300",
    sat_writing_avg_score = "300"
)