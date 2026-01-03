package com.hisaabmate.domain.registry

import android.content.Context
import java.util.Locale

data class Bank(
    val id: String, // Filename without extension (e.g., "hbl")
    val displayName: String, // Formatted Name (e.g., "HBL")
    val logoPath: String, // Asset path (file:///android_asset/banks/hbl.svg)
    val defaultColor: String
)

object BankRegistry {

    private val themeColors = mapOf(
        "hbl" to "#00833E", // Green
        "meezan_bank" to "#5C1530", // Maroon
        "ubl" to "#004899", // Blue
        "mcb" to "#009B77", // Teal
        "alfalah" to "#E31E24", // Red
        "bank_al_habib" to "#E46F25", // Orange
        "standard_chartered" to "#00563F", // Dark Green
        "jazz_cash" to "#FFCA05", // Yellow
        "easy_paisa" to "#37C240", // Green
        "sada_pay" to "#21C3A6", // Turquoise
        "naya_pay" to "#F58220", // Orange
        "citi_bank" to "#003A70" // Navy
    )

    private val acronyms = setOf("hbl", "ubl", "mcb", "idbp", "scb", "nbp", "abl", "js", "ztbl", "icbc", "nrsp", "u", "aft")

    fun getAllBanks(context: Context): List<Bank> {
        return try {
            val assets = context.assets.list("banks") ?: return emptyList()
            assets.filter { it.endsWith(".svg") }.map { filename ->
                val id = filename.substringBeforeLast(".")
                Bank(
                    id = id,
                    displayName = formatDisplayName(id),
                    logoPath = "file:///android_asset/banks/$filename",
                    defaultColor = themeColors[id] ?: generateFallbackColor(id)
                )
            }.sortedBy { it.displayName }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun formatDisplayName(id: String): String {
        return id.split("_").joinToString(" ") { word ->
            if (word.lowercase() in acronyms) {
                word.uppercase()
            } else {
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
        }
    }

    private fun generateFallbackColor(id: String): String {
        // Fallback: Generate a consistent color hash from the string if not defining all 54
        // Or default to Slate
        return "#64748B" 
    }
}
