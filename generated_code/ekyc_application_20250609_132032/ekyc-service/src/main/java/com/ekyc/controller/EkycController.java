@PostMapping("/verify-otp")
@PreAuthorize("hasRole('USER')")
@Validated
public ResponseEntity<OtpVerificationResponse> verifyOtp(
        @Valid @RequestBody OtpVerificationRequest request) {
    // Input validation
    validateOtpVerificationRequest(request);
    
    // Authentication check
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new UnauthorizedException("User not authenticated");
    }