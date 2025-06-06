@PostMapping("/config")
@Operation(summary = "Update configuration", description = "Updates mock service configuration for testing scenarios")
@PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
public ResponseEntity<String> updateConfig(
        @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
        @Valid @RequestBody AdminConfigRequest configRequest) {