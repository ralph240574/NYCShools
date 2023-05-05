package com.ralphmueller.nycschools.utils

import com.ralphmueller.nycschools.data.local.LocalSAT
import com.ralphmueller.nycschools.data.local.LocalSchool


val localSchool = LocalSchool(
    dbn = "1",
    school_name = "Hogwards",
    overview_paragraph = "Hogwards is a school of Witchcraft and Wizardry",
    location = "123 Street ",
    phone_number = "1234567890",
    school_email = "mail@school.com",
    website = "school.com",
)

val localSAT = LocalSAT(
    dbn = "1",
    school_name = "Hogwards",
    num_of_sat_test_takers = "95",
    sat_critical_reading_avg_score = "394",
    sat_math_avg_score = "414",
    sat_writing_avg_score = "376"
)
