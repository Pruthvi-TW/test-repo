@PostMapping("/initiate")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<EkycResponse> initiateEkycVerification(@Valid @RequestBody EkycRequest request) {
    logger.info("Received eKYC initiation request for session: {}", 
    // ...