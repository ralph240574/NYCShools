package com.ralphmueller.nycschools.model

/*
This class represent all the school data, which is used for the UI on the device it contains the SAT
scores as well
 */
data class School(
    val dbn: String,
    val school_name: String,
    val overview_paragraph: String,
    val location: String,
    val phone_number: String,
    val school_email: String,
    val website: String,
    val num_of_sat_test_takers: String,
    val sat_critical_reading_avg_score: String,
    val sat_math_avg_score: String,
    val sat_writing_avg_score: String
)

