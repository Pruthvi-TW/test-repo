@PostMapping("/verify")
@PreAuthorize("hasRole('INTERNAL_SERVICE')")
public ResponseEntity<UidaiVerifyResponse> verifyOtp(
        @RequestHeader("X-Trace-Id") UUID traceId,
        @AuthenticationPrincipal ServicePrincipal servicePrincipal,
        @Valid @RequestBody UidaiVerifyRequest request) {