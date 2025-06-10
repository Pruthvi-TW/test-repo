@PostMapping("/verify")
@PreAuthorize("hasRole('UIDAI_SERVICE')")
public ResponseEntity<UidaiVerifyResponse> verifyOtp(
    @RequestHeader("X-Trace-Id") String traceId,
    @RequestHeader("Authorization") String authToken,
    @Valid @RequestBody UidaiVerifyRequest request
) {
    // Validate authentication token
    authenticationService.validateToken(authToken);
    
    traceLogger.logRequest(traceId, request);
    UidaiVerifyResponse response = mockUidaiService.verifyOtp(request);
    traceLogger.logResponse(traceId, response);
    return ResponseEntity.ok(response);
}