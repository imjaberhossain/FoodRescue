package com.foodrescue.repository;

import com.foodrescue.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByHostUser_IdOrderByEventDateAsc(Long hostUserId);

    /** Every future event, optionally filtered by a city/keyword, soonest first...*/
    @Query("""
            select e from Event e
            where e.eventDate > :now
              and (lower(e.city) like lower(concat('%', :keyword, '%'))
                   or lower(e.location) like lower(concat('%', :keyword, '%')))
            order by e.eventDate asc
            """)
    List<Event> findUpcoming(@Param("now") LocalDateTime now, @Param("keyword") String keyword);

    /** Used to alert providers in the same city when a new event is posted. */
    List<Event> findByCityIgnoreCaseOrderByEventDateAsc(String city);
}
