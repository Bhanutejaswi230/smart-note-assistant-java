package com.example;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// By extending CrudRepository, Spring Data JDBC
// automatically gives you methods like save(), findAll(), findById(), etc.
// You don't have to write the SQL!
public interface NoteRepository extends CrudRepository<Note, Long> {

    /**
     * NEW: GET /api/search?query=...
     * Performs a case-insensitive search across topic, content, and keywords.
     */
    @Query("SELECT * FROM note WHERE " +
           "LOWER(topic) LIKE :term OR " +
           "LOWER(content) LIKE :term OR " +
           "LOWER(keywords) LIKE :term")
    List<Note> searchByTerm(@Param("term") String term);

}
