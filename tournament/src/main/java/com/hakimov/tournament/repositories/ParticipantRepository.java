package com.hakimov.tournament.repositories;

import com.hakimov.tournament.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
