package com.hisaabmate.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.hisaabmate.data.local.entity.TransactionEntity
import com.hisaabmate.domain.repository.HisaabRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.regex.Pattern

@AndroidEntryPoint
class HisaabNotificationListener : NotificationListenerService() {

    @Inject
    lateinit var repository: HisaabRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        
        if (packageName == "com.telenor.pakistan.easypaisa" || 
            packageName == "com.techlogix.mobilinkcustomer") {
            
            val extras = sbn.notification.extras
            val title = extras.getString("android.title")
            val text = extras.getCharSequence("android.text")?.toString() ?: return

            Log.d("HisaabListener", "Processing: $packageName | $text")
            
            parseAndSaveTransaction(text, packageName)
        }
    }

    private fun parseAndSaveTransaction(text: String, packageName: String) {
        var type = "UNKNOWN"
        var amount = 0.0
        
        // Regex to match "sent Rs. 500 to Ali via" or similar
        // Captures: Group 1 = Amount, Group 2 = Recipient (Name)
        val regex = "sent.*?Rs\\.?\\s*([\\d,]+(?:\\.\\d{2})?).*?to\\s+([A-Za-z\\s]+)\\s+via"
        val matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(text)

        var recipient = ""
        
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "")
            amount = amountStr?.toDoubleOrNull() ?: 0.0
            recipient = matcher.group(2)?.trim() ?: ""
            type = "DEBIT" // "sent" implies debit usually
        } else if (text.contains("received", ignoreCase = true)) {
             // Fallback for received/other types if we want to keep them, 
             // but for this specific task we are focusing on the "sent ... to" case.
             // We can keep the old simple check for "received" as a secondary check or just focus on the requested fix.
             // Let's keep a simple fallback for amount if the main regex fails but it's a credit.
             type = "CREDIT"
             val amountMatcher = Pattern.compile("Rs\\.?\\s*([\\d,]+(\\.\\d{2})?)").matcher(text)
             if (amountMatcher.find()) {
                 val amountStr = amountMatcher.group(1)?.replace(",", "")
                 amount = amountStr?.toDoubleOrNull() ?: 0.0
             }
        }

        if (amount > 0) {
            val source = if (packageName.contains("easypaisa")) "Easypaisa" else "JazzCash"
            val finalCategory = if (recipient.isNotEmpty()) "$source - $recipient" else source

            val transaction = TransactionEntity(
                amount = amount,
                type = type,
                category = finalCategory,
                date = System.currentTimeMillis(),
                accountId = 1 // Mapping to a default account for now
            )
            
            serviceScope.launch {
                try {
                    repository.insertTransaction(transaction)
                    Log.d("HisaabListener", "Saved Transaction: $transaction")
                } catch (e: Exception) {
                    Log.e("HisaabListener", "Error saving transaction", e)
                }
            }
        }
    }
}
