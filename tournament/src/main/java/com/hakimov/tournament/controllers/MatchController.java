package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MatchController {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final ParticipantRepository participantRepository;

    public MatchController(TournamentRepository tournamentRepository,
                           MatchRepository matchRepository,
                           ParticipantRepository participantRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/tournaments/{tournamentId}/matches/{matchId}")
    public Match summarizeMatch(@PathVariable Long tournamentId, @PathVariable Long matchId,
                                Long firstParticipantScore, Long secondParticipantScore)
            throws NotFoundException {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        Tournament tournament = tournamentOptional.get();

        if (firstParticipantScore.equals(secondParticipantScore)) {
            throw new IllegalArgumentException("Scores should be different! Tournament ID: " + tournamentId + ", match ID: " + matchId);
        }

        return matchRepository.findById(matchId)
                .map(match -> {
                    match.setFirstParticipantScore(firstParticipantScore);
                    match.setSecondParticipantScore(secondParticipantScore);
                    match.setFinishTime(LocalDateTime.now());

                    if (firstParticipantScore < secondParticipantScore) {
                        match.getParticipants().get(0).setActive(false);
                    } else {
                        match.getParticipants().get(1).setActive(false);
                    }

                    Utils.setMatchesPlayed(Utils.getMatchesPlayed() + 1);

                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println(Utils.getMatchesPlayed() + " " + Utils.getTotalMatches());

                    if (Utils.getMatchesPlayed().equals(Utils.getTotalMatches())) {
                        Utils.matchParticipants(tournament, matchRepository,
                                tournament.getParticipants(), participantRepository);
                        Utils.setMatchesPlayed(0);
                    }

                    return matchRepository.save(match);
                }).orElseThrow(() -> new NotFoundException(Utils.matchNotFoundMessage(matchId)));
    }
}
