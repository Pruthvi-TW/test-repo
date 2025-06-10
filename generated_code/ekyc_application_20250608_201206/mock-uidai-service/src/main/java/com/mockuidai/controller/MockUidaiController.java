@PostMapping("/verify")
@PreAuthorize("hasRole('UIDAI_SERVICE')")
public ResponseEntity<UidaiVerifyResponse> verifyOtp(
    @RequestHeader("X-Trace-Id") UUID traceId,
    @RequestHeader("Authorization") String authHeader,
    @Valid @RequestBody UidaiVerifyRequest request
) {
    // Validate authentication token
    authenticationService.validateToken(authHeader);
    
    traceLogger.logRequest(traceId, request);
    UidaiVerifyResponse response = mockUidaiService.verifyOtp(request);
    traceLogger.logResponse(traceId, response);
    return ResponseEntity.ok(response);
}