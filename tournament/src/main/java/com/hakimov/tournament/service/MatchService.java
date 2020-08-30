package com.hakimov.tournament.service;

import com.hakimov.tournament.exception.TournamentException;
import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;
import com.hakimov.tournament.repositories.TournamentRepository;
import com.hakimov.tournament.util.Utils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MatchService {
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final ParticipantRepository participantRepository;

    public MatchService(TournamentRepository tournamentRepository, MatchRepository matchRepository, ParticipantRepository participantRepository) {
        this.tournamentRepository = tournamentRepository;
        this.matchRepository = matchRepository;
        this.participantRepository = participantRepository;
    }

    public List<Match> findAllMatches(Long tournamentId) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        return tournamentOptional.get().getMatches();
    }

    public Match summarizeMatch(Long tournamentId,
                                Long matchId,
                                Long firstParticipantScore,
                                Long secondParticipantScore) {
        Optional<Tournament> tournamentOptional = tournamentRepository.findById(tournamentId);

        if (tournamentOptional.isEmpty()) {
            throw new TournamentException(Utils.tournamentNotFoundMessage(tournamentId));
        }

        Tournament tournament = tournamentOptional.get();

        if (firstParticipantScore.equals(secondParticipantScore)) {
            throw new TournamentException("Scores should be different! Tournament ID: " + tournamentId + ", match ID: " + matchId);
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
                }).orElseThrow(() -> new TournamentException(Utils.matchNotFoundMessage(matchId)));
    }
}
