package com.mockuidai.util;

import com.mockuidai.dto.KycData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class MockDataUtil {

    private static final List<String> FIRST_NAMES = Arrays.asList(
            "Aarav", "Vivaan", "Aditya", "Vihaan", "Arjun", "Reyansh", "Ayaan", "Atharva",
            "Aanya", "Aadhya", "Saanvi", "Ananya", "Pari", "Myra", "Siya", "Aditi"
    );

    private static final List<String> LAST_NAMES = Arrays.asList(
            "Sharma", "Verma", "Patel", "Gupta", "Singh", "Kumar", "Joshi", "Rao",
            "Malhotra", "Chopra", "Nair", "Mehta", "Jain", "Shah", "Reddy", "Kapoor"
    );

    private static final List<String> CITIES = Arrays.asList(
            "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune", "Ahmedabad",
            "Jaipur", "Lucknow", "Kochi", "Chandigarh", "Bhopal", "Indore", "Nagpur", "Surat"
    );

    private static final List<String> STATES = Arrays.asList(
            "Maharashtra", "Delhi", "Karnataka", "Telangana", "Tamil Nadu", "West Bengal", 
            "Gujarat", "Rajasthan", "Uttar Pradesh", "Kerala", "Punjab", "Madhya Pradesh"
    );

    private final Random random = new Random();

    public KycData generateKycData(String aadhaarOrVid) {
        // Use the Aadhaar/VID as a seed for consistent data generation
        long seed = 0;
        for (char c : aadhaarOrVid.toCharArray()) {
            seed = seed * 10 + (c - '0');
        }
        Random seededRandom = new Random(seed);
        
        String firstName = FIRST_NAMES.get(seededRandom.nextInt(FIRST_NAMES.size()));
        String lastName = LAST_NAMES.get(seededRandom.nextInt(LAST_NAMES.size()));
        String fullName = firstName + " " + lastName;
        
        // Generate a random date of birth between 18 and 70 years ago
        int year = LocalDate.now().getYear() - (18 + seededRandom.nextInt(53));
        int month = 1 + seededRandom.nextInt(12);
        int day = 1 + seededRandom.nextInt(28); // Simplified to avoid month length issues
        LocalDate dob = LocalDate.of(year, month, day);
        
        // Gender based on first name list position (even = M, odd = F)
        String gender = FIRST_NAMES.indexOf(firstName) % 2 == 0 ? "M" : "F";
        
        // Generate a random address
        String city = CITIES.get(seededRandom.nextInt(CITIES.size()));
        String state = STATES.get(seededRandom.nextInt(STATES.size()));
        String pincode = String.format("%06d", 100000 + seededRandom.nextInt(900000));
        String address = String.format("%d, %s Nagar, %s, %s - %s", 
                1 + seededRandom.nextInt(999), 
                FIRST_NAMES.get(seededRandom.nextInt(FIRST_NAMES.size())),
                city, state, pincode);
        
        // Generate masked mobile and email
        String maskedMobile = "XXXXXXX" + (100 + seededRandom.nextInt(900));
        String maskedEmail = firstName.toLowerCase().charAt(0) + "***@" + 
                             (seededRandom.nextBoolean() ? "g****.com" : "y****.com");
        
        return KycData.builder()
                .name(fullName)
                .dob(dob.format(DateTimeFormatter.ISO_DATE))
                .gender(gender)
                .address(address)
                .maskedMobile(maskedMobile)
                .maskedEmail(maskedEmail)
                .build();
    }
}