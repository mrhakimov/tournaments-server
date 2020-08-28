package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                    participant.setTournament(tournament);
                    
                    return participantRepository.save(participant);
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
      
        return participantRepository.findById(participantId)
                .map(participant -> {
                    participantRepository.delete(participant);

                    return "Participant deleted successfully!";
                }).orElseThrow(() -> new NotFoundException(Utils.participantNotFoundMessage()));
    }
}
