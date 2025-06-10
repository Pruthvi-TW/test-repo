package com.ekyc.dto.request;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.ekyc.enums.ConsentType;
import com.ekyc.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Request DTO for initiating the eKYC verification process.
 * Contains all necessary information to start an eKYC verification.
 */
public class EkycInitiateRequest {

    @NotNull(message = "ID type is required")
    private IdType idType;

    @NotBlank(message = "ID number is required")
    @Size(min = 10, max = 16, message = "ID number must be between 10 and 16 characters")
    private String idNumber;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s.'-]+$", message = "Full name contains invalid characters")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    private String mobileNumber;

    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "At least one consent type is required")
    private Set<ConsentType> consentTypes = new HashSet<>();

    @Size(max = 500, message = "Additional notes cannot exceed 500 characters")
    private String additionalNotes;

    // Default constructor
    public EkycInitiateRequest() {
    }

    // Getters and setters
    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<ConsentType> getConsentTypes() {
        return consentTypes;
    }

    public void setConsentTypes(Set<ConsentType> consentTypes) {
        this.consentTypes = consentTypes != null ? consentTypes : new HashSet<>();
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    /**
     * Validates if all mandatory consents are provided
     * 
     * @return true if all mandatory consents are provided
     */
    public boolean hasMandatoryConsents() {
        for (ConsentType mandatoryConsent : ConsentType.getMandatoryConsents()) {
            if (!consentTypes.contains(mandatoryConsent)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EkycInitiateRequest that = (EkycInitiateRequest) o;
        return idType == that.idType &&
               Objects.equals(idNumber, that.idNumber) &&
               Objects.equals(fullName, that.fullName) &&
               Objects.equals(dateOfBirth, that.dateOfBirth) &&
               Objects.equals(gender, that.gender) &&
               Objects.equals(mobileNumber, that.mobileNumber) &&
               Objects.equals(email, that.email) &&
               Objects.equals(consentTypes, that.consentTypes) &&
               Objects.equals(additionalNotes, that.additionalNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idType, idNumber, fullName, dateOfBirth, gender, mobileNumber, email, consentTypes, additionalNotes);
    }

    @Override
    public String toString() {
        // Masking sensitive information for logging
        return "EkycInitiateRequest{" +
                "idType=" + idType +
                ", idNumber='[MASKED]'" +
                ", fullName='[MASKED]'" +
                ", dateOfBirth=[MASKED]" +
                ", gender='" + gender + '\'' +
                ", mobileNumber='[MASKED]'" +
                ", email='[MASKED]'" +
                ", consentTypes=" + consentTypes +
                ", additionalNotes='" + (additionalNotes != null ? "[PRESENT]" : "[NOT PRESENT]") + '\'' +
                '}';
    }

    /**
     * Builder class for EkycInitiateRequest
     */
    public static class Builder {
        private IdType idType;
        private String idNumber;
        private String fullName;
        private LocalDate dateOfBirth;
        private String gender;
        private String mobileNumber;
        private String email;
        private Set<ConsentType> consentTypes = new HashSet<>();
        private String additionalNotes;

        public Builder idType(IdType idType) {
            this.idType = idType;
            return this;
        }

        public Builder idNumber(String idNumber) {
            this.idNumber = idNumber;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder mobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder consentTypes(Set<ConsentType> consentTypes) {
            this.consentTypes = consentTypes;
            return this;
        }

        public Builder addConsentType(ConsentType consentType) {
            this.consentTypes.add(consentType);
            return this;
        }

        public Builder additionalNotes(String additionalNotes) {
            this.additionalNotes = additionalNotes;
            return this;
        }

        public EkycInitiateRequest build() {
            EkycInitiateRequest request = new EkycInitiateRequest();
            request.setIdType(this.idType);
            request.setIdNumber(this.idNumber);
            request.setFullName(this.fullName);
            request.setDateOfBirth(this.dateOfBirth);
            request.setGender(this.gender);
            request.setMobileNumber(this.mobileNumber);
            request.setEmail(this.email);
            request.setConsentTypes(this.consentTypes);
            request.setAdditionalNotes(this.additionalNotes);
            return request;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}