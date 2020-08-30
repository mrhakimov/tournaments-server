package com.hakimov.tournament.controllers;

import com.hakimov.tournament.exception.TournamentException;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.service.MatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Muhammadjon Hakimov (github.com/MrHakimov)
 *
 * Controller, which provides methods to get and summarize matches.
 */
@RestController
@RequestMapping("/api")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * Additional method, which gets list of matches of provided tournament
     *
     * @param tournamentId - id of tournament.
     * @return list of matches.
     * @throws TournamentException if there is no tournament with provided id.
     */
    @GetMapping("/tournaments/{tournamentId}/matches")
    public List<Match> get(@PathVariable Long tournamentId) throws TournamentException {
        return matchService.findAllMatches(tournamentId);
    }

    /**
     * Summarizes match of tournament provided by it's id.
     *
     * @param tournamentId - id of tournament.
     * @param matchId - id of match to summarize.
     * @param firstParticipantScore - score of the first participant.
     * @param secondParticipantScore - score of the second participant.
     * @return summarized match.
     * @throws TournamentException if there is no tournament or match with provided ids.
     */
    @PutMapping("/tournaments/{tournamentId}/matches/{matchId}")
    public Match summarizeMatch(@PathVariable Long tournamentId,
                                @PathVariable Long matchId,
                                Long firstParticipantScore,
                                Long secondParticipantScore)
            throws TournamentException {
        return matchService.summarizeMatch(tournamentId, matchId, firstParticipantScore, secondParticipantScore);
    }
}
