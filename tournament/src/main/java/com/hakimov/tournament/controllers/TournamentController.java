package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final ParticipantRepository participantRepository;

    TournamentController(TournamentRepository tournamentRepository, MatchRepository matchRepository, ParticipantRepository participantRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    @PostMapping("/create")
    public void create(Integer matchesNumber, Integer maxParticipants) {
        Tournament tournament = new Tournament();
        tournament.setMatchesNumber(matchesNumber);
        tournament.setMaxParticipants(maxParticipants);
        tournamentRepository.save(tournament);
    }

    @PostMapping("/get")
    public Tournament get(Long id) {
        Optional<Tournament> result = tournamentRepository.findById(id);
        return result.orElse(null);
    }

    @PostMapping("/add")
    public void add(Long tournamentId, Long participantId, String participantNickname) {
        Participant participant = new Participant();
        participant.setId(participantId);
        participant.setNickname(participantNickname);

        participantRepository.save(participant);

        Tournament tournament = get(tournamentId);
        tournament.getParticipants().add(participant);
    }

    @PostMapping("/remove")
    public boolean remove(Long tournamentId, Long participantId) {
        Tournament tournament = get(tournamentId);

        if (tournament == null || tournament.isOnHold()) {
            return false;
        }

        int indexToRemove = -1;
        for (int i = 0; i < tournament.getParticipants().size(); ++i) {
            Participant participant = tournament.getParticipants().get(i);
            if (participant.getId().equals(participantId)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return false;
        }

        tournament.getParticipants().remove(indexToRemove);

        return true;
    }

    @PostMapping("/start")
    public boolean start(Long tournamentId) {
        Tournament tournament = get(tournamentId);

        if (tournament == null) {
            return false;
        }

        tournament.setOnHold(false);
        List<Participant> participants = tournament.getParticipants();
        Collections.shuffle(participants);
        Utils.matchParticipants(tournament, matchRepository, participants);

        return true;
    }

    @PostMapping("/hold")
    public boolean hold(Long tournamentId) {
        Tournament tournament = get(tournamentId);

        if (tournament == null) {
            return false;
        }

        tournament.setOnHold(true);
        return true;
    }
}
