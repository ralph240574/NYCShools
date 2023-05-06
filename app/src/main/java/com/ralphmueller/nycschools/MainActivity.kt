package com.ralphmueller.nycschools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ralphmueller.nycschools.data.SchoolRepo
import com.ralphmueller.nycschools.ui.detail.DetailViewModel
import com.ralphmueller.nycschools.ui.home.HomeViewModel
import com.ralphmueller.nycschools.ui.theme.RalphMuellerNYCSchoolsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var schoolRepo: SchoolRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RalphMuellerNYCSchoolsTheme {
                val homeViewModel by viewModels<HomeViewModel>()
                val detailViewModel by viewModels<DetailViewModel>()

                NavGraph(
                    homeViewModel = homeViewModel,
                    detailViewModel = detailViewModel
                )
            }
        }
    }
}
