package com.skillchain.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: INTERFACE
 * ============================================================================
 * Generic search contract establishing a standardized querying mechanism
 * across domain entities (Skills, Users, Matches).
 *
 * @param <T> The type of domain entity returned by the search query.
 */
public interface Searchable<T> {

    /**
     * Executes a keyword-based search across the underlying repository.
     *
     * @param keyword Partial or complete search term.
     * @return List of matching domain entities.
     * @throws SQLException if a database query error occurs.
     */
    List<T> search(String keyword) throws SQLException;
}
