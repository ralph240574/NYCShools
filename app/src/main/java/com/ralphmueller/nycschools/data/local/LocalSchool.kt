package com.ralphmueller.nycschools.data.local


import androidx.room.Entity
import com.ralphmueller.nycschools.model.School

@Entity(tableName = "localschool", primaryKeys = ["dbn"])
data class LocalSchool(
    val dbn: String,
    val school_name: String,
    val overview_paragraph: String,
    val location: String,
    val phone_number: String,
    val school_email: String,
    val website: String,
) {
    fun toSchool(localSAT: LocalSAT?): School {
        return School(
            dbn = dbn,
            school_name = school_name,
            overview_paragraph = overview_paragraph,
            location = location,
            phone_number = phone_number,
            school_email = school_email,
            website = website,
            num_of_sat_test_takers = localSAT?.num_of_sat_test_takers ?: "N/A",
            sat_critical_reading_avg_score = localSAT?.sat_critical_reading_avg_score ?: "N/A",
            sat_math_avg_score = localSAT?.sat_math_avg_score ?: "N/A",
            sat_writing_avg_score = localSAT?.sat_writing_avg_score ?: "N/A"
        )
    }
}