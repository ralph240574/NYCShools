package com.ralphmueller.nycschools.data.remote.model

import com.ralphmueller.nycschools.data.local.LocalSAT
import com.ralphmueller.nycschools.data.local.LocalSchool

/*
 These classes repesent the sever side json response
 for https://data.cityofnewyork.us/resource/s3k6-pzi2.json
 and https://data.cityofnewyork.us/resource/f9bf-2cp4.json
 */
data class RDSchool(
    val dbn: String,
    val school_name: String,
    val overview_paragraph: String,
    val location: String,
    val phone_number: String,
    val school_email: String?,
    val website: String?,
) {
    fun toLocalSchool(): LocalSchool {
        return LocalSchool(
            dbn = dbn,
            school_name = school_name,
            overview_paragraph = overview_paragraph,
            location = location,
            phone_number = phone_number,
            school_email = school_email ?: "",
            website = website ?: ""
        )
    }
}

data class RDSAT(
    val dbn: String,
    val school_name: String,
    val num_of_sat_test_takers: String,
    val sat_critical_reading_avg_score: String,
    val sat_math_avg_score: String,
    val sat_writing_avg_score: String
) {
    fun toLocalSAT(): LocalSAT {
        return LocalSAT(
            dbn = dbn,
            school_name = school_name,
            num_of_sat_test_takers = num_of_sat_test_takers ?: "",
            sat_critical_reading_avg_score = sat_critical_reading_avg_score ?: "",
            sat_math_avg_score = sat_math_avg_score ?: "",
            sat_writing_avg_score = sat_writing_avg_score ?: ""
        )
    }

}





