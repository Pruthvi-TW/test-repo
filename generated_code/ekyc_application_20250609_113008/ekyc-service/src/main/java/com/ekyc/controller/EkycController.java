@PostMapping("/verify-otp")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<OtpVerificationResponse> verifyOtp(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody OtpVerificationRequest request) {