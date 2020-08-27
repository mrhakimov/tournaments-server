package com.hakimov.tournament.controllers;

import com.hakimov.tournament.Utils;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.repositories.MatchRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class MatchController {
    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping("/summarize")
    public boolean summarizeMatch(Long matchId, Long firstParticipantScore, Long secondParticipantScore) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isEmpty()) {
            return false;
        }

        if (firstParticipantScore.equals(secondParticipantScore)) {
            return false;
        }

        Match match = matchOptional.get();

        if (match.getParticipants().size() != 2) {
            return false;
        }

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

        return true;
    }
}
