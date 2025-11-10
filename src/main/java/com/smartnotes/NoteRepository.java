package com.bhanu.smartnotes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface NoteRepository extends CrudRepository<Note, Long> {

    @Query("SELECT * FROM note WHERE " +
           "LOWER(topic) LIKE :term OR " +
           "LOWER(content) LIKE :term OR " +
           "LOWER(keywords) LIKE :term")
    List<Note> searchByTerm(@Param("term") String term);

}
