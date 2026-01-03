package com.hisaabmate.data.remote

import org.jsoup.Jsoup
import java.io.IOException

class GoldRateScraper {

    fun fetchGoldRate(): Double? {
        val url = "https://hamariweb.com/finance/gold_rate/"
        return try {
            val doc = Jsoup.connect(url).get()
            // Selector logic would depend on the actual site structure.
            // Assuming a table structure, often the first rate on these sites is 24K.
            // This is a placeholder selector logic based on common patterns.
            // For robustness, we would inspect the specific site HTML.
            
            // Example Strategy: specific class or id
            // val rateElement = doc.select(".rate_table tr:nth-child(2) td:nth-child(2)").text()
            
            // Placeholder: Returning a dummy value to avoid crash if selector fails, 
            // but in real app we'd need exact selectors.
            // Let's implement a generic search for "24K" text just to show logic.
            
            val elements = doc.select("td")
            var rate: Double? = null
            
            for (i in 0 until elements.size) {
                 if (elements[i].text().contains("24K", ignoreCase = true)) {
                     // Next element might be the rate
                     if (i + 1 < elements.size) {
                         val rateText = elements[i+1].text().replace(",", "").replace("PKR", "").trim()
                         rate = rateText.toDoubleOrNull()
                         if (rate != null) break
                     }
                 }
            }
            
            rate
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
