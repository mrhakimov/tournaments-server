package com.hakimov.tournament.controllers;

import com.hakimov.tournament.util.Utils;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Muhammadjon Hakimov (github.com/MrHakimov)
 *
 * Controller, which provides methods to get, add, update and delete tournaments' participants.
 */
@RestController
@RequestMapping("/api")
public class ParticipantController {
	private final ParticipantRepository participantRepository;
	private final TournamentRepository tournamentRepository;

    public ParticipantController(ParticipantRepository participantRepository, TournamentRepository tournamentRepository) {
        this.participantRepository = participantRepository;
        this.tournamentRepository = tournamentRepository;
    }

    /**
     * Gets list of participants of tournament, provided by it's id.
     * @param tournamentId - id of tournament.
     * @return list of participants.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @GetMapping("/tournaments/{tournamentId}/participants")
    public List<Participant> get(@PathVariable Long tournamentId) throws NotFoundException {
        if (!tournamentRepository.existsById(tournamentId)) {
        	throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        return participantRepository.findByTournamentId(tournamentId);
    }

    /**
     * Creates new participant for tournament provided by id.
     *
     * @param tournamentId - id of tournament.
     * @param participant - participant to add to tournament.
     * @return created participant.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @PostMapping("/tournaments/{tournamentId}/participants")
    public Participant add(@PathVariable Long tournamentId, @RequestBody Participant participant) throws NotFoundException {
        return tournamentRepository.findById(tournamentId)
                .map(tournament -> {
                    if (tournament.getParticipants().size() < tournament.getMaxParticipants()) {
                        participant.setActive(true);
                        participant.setMatch(new Match());
                        participant.setTournament(tournament);

                        return participantRepository.save(participant);
                    }

                    // TODO - size limit exceeded message
                    return null;
                }).orElseThrow(() -> new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId)));
    }

    /**
     * Additional method to update participants of tournament provided by id.
     *
     * @param tournamentId - id of tournament.
     * @param participantId - id of participant to update.
     * @param participantUpdated - updated participant.
     * @return updated participant.
     * @throws NotFoundException if there is no tournament or participant with provided ids.
     */
    @PutMapping("/tournaments/{tournamentId}/participants/{participantId}")
    public Participant update(@PathVariable Long tournamentId, @PathVariable Long participantId, @RequestBody Participant participantUpdated) throws NotFoundException {
    	if (!tournamentRepository.existsById(tournamentId)) {
        	throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
      	}
      
        return participantRepository.findById(participantId)
                .map(participant -> {
                    participant.setId(participantUpdated.getId());
                    participant.setNickname(participantUpdated.getNickname());
                    participant.setActive(participantUpdated.isActive());

                    return participantRepository.save(participant);
                }).orElseThrow(() -> new NotFoundException(Utils.participantNotFoundMessage()));
    }

    /**
     * Deletes provided participant from tournament.
     *
     * @param tournamentId - id of tournament.
     * @param participantId - id of participant to delete.
     * @return status message.
     * @throws NotFoundException if there is no tournament or participant with provided ids.
     */
    @DeleteMapping("/tournaments/{tournamentId}/participants/{participantId}")
    public String remove(@PathVariable Long tournamentId, @PathVariable Long participantId) throws NotFoundException {
    	if (!tournamentRepository.existsById(tournamentId)) {
        	throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
      	}

        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        Tournament tournament = tournamentOptional.get();

        if (!tournament.isOnHold()) {
            return "Can't delete participant, because tournament is not on hold!";
        }
      
        return participantRepository.findById(participantId)
                .map(participant -> {
                    participantRepository.delete(participant);

                    return "Participant deleted successfully!";
                }).orElseThrow(() -> new NotFoundException(Utils.participantNotFoundMessage()));
    }
}
