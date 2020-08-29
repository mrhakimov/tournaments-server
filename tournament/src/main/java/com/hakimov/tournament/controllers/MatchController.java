package com.hakimov.tournament.controllers;

import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.util.Utils;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Muhammadjon Hakimov (github.com/MrHakimov)
 *
 * Controller, which provides methods to get and summarize matches.
 */
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

    /**
     * Additional method, which gets list of matches of provided tournament
     *
     * @param tournamentId - id of tournament.
     * @return list of matches.
     * @throws NotFoundException if there is no tournament with provided id.
     */
    @GetMapping("/tournaments/{tournamentId}/matches")
    public List<Match> get(@PathVariable Long tournamentId) throws NotFoundException {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            throw new NotFoundException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        return tournamentOptional.get().getMatches();
    }

    /**
     * Summarizes match of tournament provided by it's id.
     *
     * @param tournamentId - id of tournament.
     * @param matchId - id of match to summarize.
     * @param firstParticipantScore - score of the first participant.
     * @param secondParticipantScore - score of the second participant.
     * @return summarized match.
     * @throws NotFoundException if there is no tournament or match with provided ids.
     */
    @PutMapping("/tournaments/{tournamentId}/matches/{matchId}")
    public Match summarizeMatch(@PathVariable Long tournamentId,
                                @PathVariable Long matchId,
                                Long firstParticipantScore,
                                Long secondParticipantScore)
            throws NotFoundException {
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
                    if (match.getFinishTime() != null) {
                        return match;
                    }

                    match.setFirstParticipantScore(firstParticipantScore);
                    match.setSecondParticipantScore(secondParticipantScore);
                    match.setFinishTime(LocalDateTime.now());

                    Optional<Participant> firstParticipantOptional =
                            participantRepository.findById(match.getFirstParticipantId());
                    if (firstParticipantOptional.isEmpty()) {
                        return match;
                    }
                    Participant firstParticipant = firstParticipantOptional.get();

                    Optional<Participant> secondParticipantOptional =
                            participantRepository.findById(match.getSecondParticipantId());
                    if (secondParticipantOptional.isEmpty()) {
                        return match;
                    }
                    Participant secondParticipant = secondParticipantOptional.get();

                    if (firstParticipantScore < secondParticipantScore) {
                        firstParticipant.setActive(false);
                    } else {
                        secondParticipant.setActive(false);
                    }

                    Utils.setMatchesPlayed(Utils.getMatchesPlayed() + 1);

                    if (Utils.getMatchesPlayed().equals(Utils.getTotalMatches())) {
                        Utils.matchParticipants(tournament, matchRepository,
                                tournament.getParticipants(), participantRepository);
                        Utils.setMatchesPlayed(0);
                    }

                    participantRepository.save(firstParticipant);
                    participantRepository.save(secondParticipant);

                    return matchRepository.save(match);
                }).orElseThrow(() -> new NotFoundException(Utils.matchNotFoundMessage(matchId)));
    }
}
