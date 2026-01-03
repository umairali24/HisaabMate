package com.hisaabmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val goal_id: String = UUID.randomUUID().toString(),
    val goal_name: String,
    val target_amount: Double,
    val saved_amount: Double = 0.0,
    val deadline_date: Long,
    val category_icon: String, // Stitch asset name
    val color_hex: String
)
