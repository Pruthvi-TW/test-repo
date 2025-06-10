@PostMapping("/initiate")
@PreAuthorize("hasRole('INTERNAL_SERVICE')")
public ResponseEntity<UidaiInitiateResponse> initiateOtp(
    @RequestHeader("X-Internal-Token") String internalToken,