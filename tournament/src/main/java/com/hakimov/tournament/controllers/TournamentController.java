package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final ParticipantRepository participantRepository;

    public TournamentController(TournamentRepository tournamentRepository, MatchRepository matchRepository, ParticipantRepository participantRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/tournaments/{id}")
    public Tournament get(@PathVariable Long id) throws NotFoundException {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(id);

        return tournamentOptional.orElseThrow(() -> new NotFoundException(Utils.tournamentNotFoundMessage(id)));
    }

    @PostMapping("/tournaments")
    public Tournament create(@RequestBody Tournament tournament)  throws IllegalArgumentException {
        if (tournament.getMaxParticipants() == null || tournament.getMaxParticipants() % 8 != 0) {
            throw new IllegalArgumentException("Max number of participants should be multiple of 8!");
        }

        return tournamentRepository.save(tournament);
    }

    @PutMapping("/tournaments/{id}/start")
    public String start(@PathVariable Long id) throws NotFoundException {
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(id));
        }

        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournament.setOnHold(false);
                    List<Participant> participants = tournament.getParticipants();
                    Collections.shuffle(participants);

                    String tmp = "";
                    for (Participant participant : participants) {
                        tmp += participant.getNickname() + "\n";
                    }

                    System.out.println(tmp);

                    Utils.matchParticipants(tournament, matchRepository, participants, participantRepository);

                    return "Tournament started successfully!\n" + tmp;
                }).orElseThrow(() -> new NotFoundException("Participant not found!"));
    }

    @PutMapping("/tournaments/{id}/hold")
    public String hold(@PathVariable Long id) throws NotFoundException {
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(id));
        }

        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournament.setOnHold(true);

                    return "Tournament is now on hold!";
                }).orElseThrow(() -> new NotFoundException(Utils.tournamentNotFoundMessage(id)));
    }

    @PutMapping("/tournaments/{id}")
    public Tournament update(@PathVariable Long id, @RequestBody Tournament tournamentUpdated) throws NotFoundException {
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(id));
        }

        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournament.setMaxParticipants(tournamentUpdated.getMaxParticipants());
                    tournament.setMatchesNumber(tournamentUpdated.getMatchesNumber());
                    tournament.setOnHold(tournamentUpdated.isOnHold());
                    tournament.setParticipants(tournamentUpdated.getParticipants());

                    return tournamentRepository.save(tournament);
                }).orElseThrow(() -> new NotFoundException(Utils.tournamentNotFoundMessage(id)));
    }

    @DeleteMapping("/tournaments/{id}")
    public String remove(@PathVariable Long id) throws NotFoundException {
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(id));
        }

        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournamentRepository.delete(tournament);

                    return "Tournament deleted successfully!";
                }).orElseThrow(() -> new NotFoundException(Utils.tournamentNotFoundMessage(id)));
    }
}
