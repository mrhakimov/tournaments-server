package com.hakimov.tournament.controllers;

import com.hakimov.tournament.util.Utils;
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

/**
 * @author Muhammadjon Hakimov (github.com/MrHakimov)
 *
 * Controller, which provides methods to create, get and start tournaments and some additional methods.
 */
@RestController
@RequestMapping("/api")
public class TournamentController {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final ParticipantRepository participantRepository;

    public TournamentController(TournamentRepository tournamentRepository,
                                MatchRepository matchRepository,
                                ParticipantRepository participantRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Additional method, which returns all available tournaments.
     *
     * @return all available tournaments.
     */
    @GetMapping("/tournaments")
    public List<Tournament> get() {
        return tournamentRepository.findAll();
    }

    /**
     * Gets tournament by id.
     *
     * @param id - tournaments' id.
     * @return tournament by id.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @GetMapping("/tournaments/{id}")
    public Tournament get(@PathVariable Long id) throws NotFoundException {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(id);

        return tournamentOptional.orElseThrow(() -> new NotFoundException(Utils.tournamentNotFoundMessage(id)));
    }

    /**
     * Creates new tournament.
     *
     * @param tournament - tournament to create.
     * @return created tournament.
     * @throws IllegalArgumentException if maxParticipants is not provided or is not multiple of 8.
     */
    @PostMapping("/tournaments")
    public Tournament create(@RequestBody Tournament tournament)  throws IllegalArgumentException {
        if (tournament.getMaxParticipants() == null || tournament.getMaxParticipants() % 8 != 0) {
            throw new IllegalArgumentException("Max number of participants should be multiple of 8!");
        }

        return tournamentRepository.save(tournament);
    }

    /**
     * Starts tournament with provided id.
     *
     * @param id - id of tournament to start.
     * @return status message.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @PutMapping("/tournaments/start/{id}")
    public String start(@PathVariable Long id) throws NotFoundException {
        if (!tournamentRepository.existsById(id)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(id));
        }

        return tournamentRepository.findById(id)
                .map(tournament -> {
                    tournament.setOnHold(false);
                    List<Participant> participants = tournament.getParticipants();
                    Collections.shuffle(participants);
                    tournament.setParticipants(participants);
                    Utils.matchParticipants(tournament, matchRepository, participants, participantRepository);

                    return "Tournament started successfully!\n";
                }).orElseThrow(() -> new NotFoundException("Participant not found!"));
    }

    /**
     * Additional method, which holds tournament with provided id.
     *
     * @param id - id of tournament to hold.
     * @return status message.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @PutMapping("/tournaments/hold/{id}")
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

    /**
     * Additional method, which provides interface to update tournament's fields.
     *
     * @param id - id of tournament to update.
     * @param tournamentUpdated - updated tournament.
     * @return updated tournament.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @PutMapping("/tournaments/{id}")
    public Tournament update(@PathVariable Long id,
                             @RequestBody Tournament tournamentUpdated) throws NotFoundException {
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

    /**
     * Additional method, which provides interface to delete tournament by id.
     *
     * @param id - id of tournament to delete.
     * @return status message.
     * @throws NotFoundException if there is no tournament with provided id.
     */
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
