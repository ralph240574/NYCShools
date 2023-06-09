package com.ralphmueller.nycschools.data.local


import androidx.room.Entity

/*
 This class repesents the sat scores that is stored with room
 */
@Entity(tableName = "localsat", primaryKeys = ["dbn"])
data class LocalSAT(
    val dbn: String,
    val school_name: String,
    val num_of_sat_test_takers: String,
    val sat_critical_reading_avg_score: String,
    val sat_math_avg_score: String,
    val sat_writing_avg_score: String
)

