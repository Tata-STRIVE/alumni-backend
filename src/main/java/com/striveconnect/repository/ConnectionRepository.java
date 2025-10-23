package com.striveconnect.repository;

import com.striveconnect.entity.Connection;
import com.striveconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    /**
     * Checks if a connection request already exists between two users, regardless of direction.
     */
    @Query("SELECT c FROM Connection c WHERE (c.requester = ?1 AND c.receiver = ?2) OR (c.requester = ?2 AND c.receiver = ?1)")
    Optional<Connection> findConnectionBetweenUsers(User user1, User user2);

    /**
     * Finds all connection requests sent to a specific user that have a certain status.
     * Used to find pending incoming requests.
     */
    List<Connection> findByReceiverAndStatus(User receiver, Connection.ConnectionStatus status);

    /**
     * Finds all connections initiated by a specific user that have a certain status.
     * Used to find accepted connections where the user was the requester.
     */
    List<Connection> findByRequesterAndStatus(User requester, Connection.ConnectionStatus status);
}

