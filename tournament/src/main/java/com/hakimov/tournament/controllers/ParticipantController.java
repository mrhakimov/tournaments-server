package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.naming.SizeLimitExceededException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ParticipantController {
	private final ParticipantRepository participantRepository;
	private final TournamentRepository tournamentRepository;

    public ParticipantController(ParticipantRepository participantRepository, TournamentRepository tournamentRepository) {
        this.participantRepository = participantRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @GetMapping("/tournaments/{tournamentId}/participants")
    public List<Participant> get(@PathVariable Long tournamentId) throws NotFoundException {
        if (!tournamentRepository.existsById(tournamentId)) {
        	throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        return participantRepository.findByTournamentId(tournamentId);
    }

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
