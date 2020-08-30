package com.hakimov.tournament.controllers;

import com.hakimov.tournament.exception.TournamentException;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.service.ParticipantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Muhammadjon Hakimov (github.com/MrHakimov)
 *
 * Controller, which provides methods to get, add, update and delete tournaments' participants.
 */
@RestController
@RequestMapping("/api")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    /**
     * Gets list of participants of tournament, provided by it's id.
     * @param tournamentId - id of tournament.
     * @return list of participants.
     * @throws TournamentException if there is no tournament with provided id.
     */
    @GetMapping("/tournaments/{tournamentId}/participants")
    public List<Participant> get(@PathVariable Long tournamentId) throws TournamentException {
        return participantService.findAllParticipants(tournamentId);
    }

    /**
     * Creates new participant for tournament provided by id.
     *
     * @param tournamentId - id of tournament.
     * @param participant - participant to add to tournament.
     * @return created participant.
     * @throws TournamentException if there is no tournament with provided id.
     */
    @PostMapping("/tournaments/{tournamentId}/participants")
    public Participant add(@PathVariable Long tournamentId,
                           @RequestBody Participant participant) throws TournamentException {
        return participantService.addParticipant(tournamentId, participant);
    }

    /**
     * Additional method to update participants of tournament provided by id.
     *
     * @param tournamentId - id of tournament.
     * @param participantId - id of participant to update.
     * @param participantUpdated - updated participant.
     * @return updated participant.
     * @throws TournamentException if there is no tournament or participant with provided ids.
     */
    @PutMapping("/tournaments/{tournamentId}/participants/{participantId}")
    public Participant update(@PathVariable Long tournamentId,
                              @PathVariable Long participantId,
                              @RequestBody Participant participantUpdated) throws TournamentException {
    	return participantService.updateParticipant(tournamentId, participantId, participantUpdated);
    }

    /**
     * Deletes provided participant from tournament.
     *
     * @param tournamentId - id of tournament.
     * @param participantId - id of participant to delete.
     * @return status message.
     * @throws TournamentException if there is no tournament or participant with provided ids.
     */
    @DeleteMapping("/tournaments/{tournamentId}/participants/{participantId}")
    public String remove(@PathVariable Long tournamentId,
                         @PathVariable Long participantId) throws TournamentException {
    	return participantService.removeParticipant(tournamentId, participantId);
    }
}
