package com.ralphmueller.nycschools.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ralphmueller.nycschools.ui.home.school

@Preview
@Composable
fun Detail(
    uiState: DetailUiState = DetailUiState(school = school),
    loadSchoolData: (String) -> Unit = {},
    dbn: String = "",
    popBackStack: () -> Unit = {}
) {

    LaunchedEffect(dbn) {
        loadSchoolData(dbn)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Details") },
                navigationIcon = {
                    IconButton(onClick = popBackStack) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
                actions = {
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Share, null)
                    }
                })
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(text = uiState.school?.school_name ?: "")
                Text(text = uiState.school?.school_email ?: "")
                Text(text = uiState.school?.website ?: "")
                Text(text = uiState.school?.sat_math_avg_score ?: "")
                Text(text = uiState.school?.sat_critical_reading_avg_score ?: "")
                Text(text = uiState.school?.sat_writing_avg_score ?: "")
                Text(text = uiState.school?.num_of_sat_test_takers ?: "")
            }
        }
    }
}

