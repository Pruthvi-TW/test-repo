@PostMapping("/initiate")
@PreAuthorize("hasRole('INTERNAL_SERVICE')")
public ResponseEntity<UidaiInitiateResponse> initiateOtp(
    @RequestHeader("X-Trace-Id") UUID traceId,
    @RequestHeader("X-API-Key") String apiKey,
    @Valid @RequestBody UidaiInitiateRequest request
) {
    // Validate API key
    if (!apiKeyService.isValidApiKey(apiKey)) {
        throw new UnauthorizedException("Invalid API Key");
    }