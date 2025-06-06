@PostMapping("/verify")
@Operation(summary = "Verify OTP for eKYC", description = "Verifies OTP and returns eKYC data")
@PreAuthorize("isAuthenticated() and hasRole('UIDAI_CLIENT')")