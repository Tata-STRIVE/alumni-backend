package com.striveconnect.service;

import com.striveconnect.dto.ConnectionDto;
import com.striveconnect.entity.Connection;
import com.striveconnect.entity.User;
import com.striveconnect.repository.ConnectionRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    // ... existing sendConnectionRequest method ...

    /**
     * Responds to a pending connection request (accepts or declines).
     */
    public void respondToRequest(Long connectionId, boolean accept) {
        User currentUser = getCurrentUser();
        Connection request = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connection request not found."));

        // Security: Ensure the current user is the intended receiver of the request
        if (!request.getReceiver().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to respond to this request.");
        }

        // Business Logic: Can only respond to PENDING requests
        if (request.getStatus() != Connection.ConnectionStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This request has already been responded to.");
        }

        request.setStatus(accept ? Connection.ConnectionStatus.ACCEPTED : Connection.ConnectionStatus.DECLINED);
        connectionRepository.save(request);
    }

    /**
     * Gets all pending incoming requests for the current user.
     */
    public List<ConnectionDto> getPendingRequests() {
        User currentUser = getCurrentUser();
        List<Connection> requests = connectionRepository.findByReceiverAndStatus(currentUser, Connection.ConnectionStatus.PENDING);
        return requests.stream()
                .map(this::convertConnectionToDtoForReceiver)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all accepted connections for the current user.
     */
    public List<ConnectionDto> getAcceptedConnections() {
        User currentUser = getCurrentUser();
        // Find connections where the user is either the requester OR the receiver
        List<Connection> asRequester = connectionRepository.findByRequesterAndStatus(currentUser, Connection.ConnectionStatus.ACCEPTED);
        List<Connection> asReceiver = connectionRepository.findByReceiverAndStatus(currentUser, Connection.ConnectionStatus.ACCEPTED);

        // Combine the lists and map to DTOs
        return Stream.concat(asRequester.stream(), asReceiver.stream())
                .map(conn -> convertConnectionToDtoForConnectionList(conn, currentUser.getUserId()))
                .collect(Collectors.toList());
    }


    // --- Helper and DTO Conversion Methods ---

    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found in database."));
    }

    // When viewing a request, we want to show the requester's details
    private ConnectionDto convertConnectionToDtoForReceiver(Connection connection) {
        ConnectionDto dto = new ConnectionDto();
        dto.setConnectionId(connection.getConnectionId());
        dto.setUserId(connection.getRequester().getUserId());
        dto.setUserName(connection.getRequester().getFullName());
        dto.setStatus(connection.getStatus().name());
        return dto;
    }
    
    // When viewing an accepted connection list, we want to show the OTHER person's details
    private ConnectionDto convertConnectionToDtoForConnectionList(Connection connection, String currentUserId) {
        User otherUser = connection.getRequester().getUserId().equals(currentUserId)
                ? connection.getReceiver()
                : connection.getRequester();
        
        ConnectionDto dto = new ConnectionDto();
        dto.setConnectionId(connection.getConnectionId());
        dto.setUserId(otherUser.getUserId());
        dto.setUserName(otherUser.getFullName());
        dto.setStatus(connection.getStatus().name());
        return dto;
    }
    
    // ... existing sendConnectionRequest method ...
    public void sendConnectionRequest(String receiverId) {
        User requester = getCurrentUser();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver user not found"));
        
        // Security: Ensure both users are in the same tenant
        if (!requester.getTenantId().equals(receiver.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Users must be in the same organization to connect.");
        }

        // Business Logic: Prevent connecting with oneself
        if (requester.getUserId().equals(receiver.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot send a connection request to yourself.");
        }

        // Business Logic: Prevent duplicate requests
        connectionRepository.findConnectionBetweenUsers(requester, receiver).ifPresent(c -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A connection request already exists between you and this user.");
        });

        Connection newConnection = new Connection();
        newConnection.setRequester(requester);
        newConnection.setReceiver(receiver);
        newConnection.setStatus(Connection.ConnectionStatus.PENDING);
        newConnection.setRequestedAt(LocalDateTime.now());

        connectionRepository.save(newConnection);
    }
}

