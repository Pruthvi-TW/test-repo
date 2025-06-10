package com.mockuidai.util;

import com.mockuidai.dto.KycData;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class MockDataUtil {

    private final Random random = new Random();

    private final List<String> maleFirstNames = Arrays.asList(
            "Rahul", "Amit", "Vijay", "Rajesh", "Sunil", "Anil", "Suresh", "Ramesh", "Ajay", "Sanjay",
            "Vikram", "Ravi", "Deepak", "Manish", "Aditya", "Nitin", "Saurabh", "Ankit", "Rohit", "Mohit"
    );

    private final List<String> femaleFirstNames = Arrays.asList(
            "Priya", "Neha", "Pooja", "Anjali", "Swati", "Ritu", "Kavita", "Sunita", "Anita", "Meena",
            "Geeta", "Sita", "Nisha", "Manju", "Rekha", "Shilpa", "Deepika", "Aishwarya", "Sneha", "Divya"
    );

    private final List<String> lastNames = Arrays.asList(
            "Kumar", "Singh", "Sharma", "Verma", "Patel", "Gupta", "Jain", "Shah", "Rao", "Reddy",
            "Patil", "Nair", "Joshi", "Chauhan", "Yadav", "Tiwari", "Mishra", "Pandey", "Agarwal", "Mehta"
    );

    private final List<String> addresses = Arrays.asList(
            "123 Main Street, Bangalore, Karnataka, 560001",
            "456 Park Avenue, Mumbai, Maharashtra, 400001",
            "789 Gandhi Road, Delhi, Delhi, 110001",
            "321 Lake View, Chennai, Tamil Nadu, 600001",
            "654 Hill Side, Hyderabad, Telangana, 500001",
            "987 River Front, Kolkata, West Bengal, 700001",
            "159 Temple Road, Pune, Maharashtra, 411001",
            "753 Market Lane, Ahmedabad, Gujarat, 380001",
            "246 Beach Road, Kochi, Kerala, 682001",
            "135 Valley View, Jaipur, Rajasthan, 302001"
    );

    /**
     * Generates mock KYC data based on the Aadhaar/VID
     */
    public KycData generateKycData(String aadhaarOrVid) {
        // Use the Aadhaar/VID as a seed for consistent data generation
        long seed = 0;
        for (char c : aadhaarOrVid.toCharArray()) {
            seed = seed * 10 + Character.getNumericValue(c);
        }
        random.setSeed(seed);

        // Determine gender (even last digit = male, odd = female)
        char lastDigit = aadhaarOrVid.charAt(aadhaarOrVid.length() - 1);
        boolean isMale = Character.getNumericValue(lastDigit) % 2 == 0;
        String gender = isMale ? "M" : "F";

        // Generate name
        String firstName = isMale 
                ? maleFirstNames.get(random.nextInt(maleFirstNames.size()))
                : femaleFirstNames.get(random.nextInt(femaleFirstNames.size()));
        String lastName = lastNames.get(random.nextInt(lastNames.size()));
        String fullName = firstName + " " + lastName;

        // Generate date of birth (18-70 years old)
        int year = LocalDate.now().getYear() - 18 - random.nextInt(53);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28); // Simplified to avoid month-specific day calculations
        LocalDate dob = LocalDate.of(year, month, day);

        // Generate address
        String address = addresses.get(random.nextInt(addresses.size()));

        // Generate masked mobile and email
        String maskedMobile = String.format("%02d****%04d", random.nextInt(100), random.nextInt(10000));
        String maskedEmail = firstName.toLowerCase().substring(0, Math.min(2, firstName.length())) + 
                "***@" + (random.nextBoolean() ? "gmail.com" : "yahoo.com");

        return KycData.builder()
                .name(fullName)
                .dob(dob)
                .gender(gender)
                .address(address)
                .maskedMobile(maskedMobile)
                .maskedEmail(maskedEmail)
                .build();
    }
}