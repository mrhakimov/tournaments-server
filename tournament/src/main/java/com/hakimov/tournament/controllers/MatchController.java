package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class MatchController {
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public MatchController(MatchRepository matchRepository, TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @GetMapping("/tournaments/{tournamentId}/matches/{matchId}")
    public String summarizeMatch(@PathVariable Long tournamentId, @PathVariable Long matchId, Long firstParticipantScore, Long secondParticipantScore) throws NotFoundException {
        if (!tournamentRepository.existsById(tournamentId)) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

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

                    if (Utils.getMatchesPlayed().equals(Utils.getTotalMatches())) {
                        Utils.setMatchesPlayed(0);
                    }

                    return "Match summarized successfully!";
                }).orElseThrow(() -> new NotFoundException(Utils.matchNotFoundMessage(matchId)));
    }
}
