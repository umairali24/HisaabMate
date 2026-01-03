import re

def test_regex():
    text = "You have sent Rs. 500 to Ali via Easypaisa"
    print(f"Testing Text: '{text}'")
    
    # -----------------------------------------------------
    # Logic copied from HisaabNotificationListener.kt (Python equivalent)
    # -----------------------------------------------------
    
    # Regex to match "sent Rs. 500 to Ali via" or similar
    # Captures: Group 1 = Amount, Group 2 = Recipient (Name)
    # Kotlin: "sent.*?Rs\\.?\\s*([\\d,]+(?:\\.\\d{2})?).*?to\\s+([A-Za-z\\s]+)\\s+via"
    # Python Raw String: r"sent.*?Rs\.?\s*([\d,]+(?:\.\d{2})?).*?to\s+([A-Za-z\s]+)\s+via"
    
    regex_pattern = r"sent.*?Rs\.?\s*([\d,]+(?:\.\d{2})?).*?to\s+([A-Za-z\s]+)\s+via"
    
    match = re.search(regex_pattern, text, re.IGNORECASE)
    
    amount = 0.0
    recipient = ""
    
    if match:
        amount_str = match.group(1).replace(",", "")
        amount = float(amount_str)
        recipient = match.group(2).strip()
        print("MATCH FOUND!")
        print(f"Group 1 (Amount Raw): {match.group(1)}")
        print(f"Group 2 (Recipient): {match.group(2)}")
    else:
        print("NO MATCH FOUND")

    # -----------------------------------------------------
    # Verification
    # -----------------------------------------------------
    print(f"Extracted Amount: {amount}")
    print(f"Extracted Recipient: '{recipient}'")
    
    if amount == 500.0 and recipient == "Ali":
        print("Status: SUCCESS")
    else:
        print("Status: FAILURE")

if __name__ == "__main__":
    test_regex()
