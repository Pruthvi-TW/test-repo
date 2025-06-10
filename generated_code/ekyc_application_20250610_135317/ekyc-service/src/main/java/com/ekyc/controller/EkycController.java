@PostMapping("/verify-otp")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> verifyOtp(
    @RequestParam @NotBlank String referenceNumber,
    @RequestParam @NotBlank String otp,
    Principal principal
) {
    // Validate input parameters
    validateOtpVerificationRequest(referenceNumber, otp);
    
    // Additional security checks
    if (!isValidOtpLength(otp)) {
        throw new ValidationException("Invalid OTP format");
    }