package com.hakimov.tournament.repositories;

import com.hakimov.tournament.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    /**
     * Return participants of tournament by id.
     * @param id - tournament id
     * @return - list of tournament's participants
     */
    List<Participant> findByTournamentId(Long id);
}
