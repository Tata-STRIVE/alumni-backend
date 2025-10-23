package com.striveconnect.controller;

import com.striveconnect.dto.ConnectionDto;
import com.striveconnect.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Sends a connection request to another user.
     */
    @PostMapping("/request/{receiverId}")
    public ResponseEntity<?> sendRequest(@PathVariable String receiverId) {
        connectionService.sendConnectionRequest(receiverId);
        return ResponseEntity.ok(Map.of("message", "Connection request sent successfully."));
    }

    /**
     * Gets all pending incoming connection requests for the current user.
     */
    @GetMapping("/requests")
    public ResponseEntity<List<ConnectionDto>> getPendingRequests() {
        List<ConnectionDto> requests = connectionService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * Accepts a pending connection request.
     */
    @PostMapping("/accept/{connectionId}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long connectionId) {
        connectionService.respondToRequest(connectionId, true);
        return ResponseEntity.ok(Map.of("message", "Connection request accepted."));
    }

    /**
     * Declines a pending connection request.
     */
    @PostMapping("/decline/{connectionId}")
    public ResponseEntity<?> declineRequest(@PathVariable Long connectionId) {
        connectionService.respondToRequest(connectionId, false);
        return ResponseEntity.ok(Map.of("message", "Connection request declined."));
    }

    /**
     * Gets all of the current user's accepted connections.
     */
    @GetMapping
    public ResponseEntity<List<ConnectionDto>> getAcceptedConnections() {
        List<ConnectionDto> connections = connectionService.getAcceptedConnections();
        return ResponseEntity.ok(connections);
    }
}

