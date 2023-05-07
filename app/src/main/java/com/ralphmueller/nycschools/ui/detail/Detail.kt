package com.ralphmueller.nycschools.ui.detail

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ralphmueller.nycschools.model.School
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
                    val context = LocalContext.current
                    IconButton(onClick = {

                        val school = uiState.school
                        if (school != null) {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, getShareText(school))
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    }) {
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

fun getShareText(school: School?): String {
    if (school == null) {
        return ""
    }
    return school.school_name + "\n" + school.website + "\n" + school.school_email

}
