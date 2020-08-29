package com.hakimov.tournament.util;

import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Muhammadjon Hakimov (github.com/MrHakimov)
 *
 * Helper class for rendering the common code.
 */
public class Utils {
    /**
     * The main idea of matches' construction is separation to layers. So, totalMatches stores
     * the number of matches on the current layer.
     */
    private static Integer totalMatches;

    /**
     * The number of played matches in the current layer.
     */
    private static Integer matchesPlayed = 0;

    /**
     * Rendered not found message for tournaments.
     *
     * @param id - tournament id.
     * @return not found message.
     */
    public static String tournamentNotFoundMessage(Long id) {
        return "Can't find tournament with id: " + id + "!";
    }

    /**
     * Rendered not found message for participants.
     *
     * @return not found message.
     */
    public static String participantNotFoundMessage() {
        return "Can't find participant!";
    }

    /**
     * Rendered not found message for matches.
     *
     * @param id - match id.
     * @return not found message.
     */
    public static String matchNotFoundMessage(Long id) {
        return "Can't find match with id: " + id + "!";
    }

    /**
     * The main algorithm of separation to layers.
     *
     * @param tournament - tournament to work with.
     * @param matchRepository - matchRepository for saving updates.
     * @param participants - participants' list of tournament.
     * @param participantRepository - participantRepository for saving updates of participants.
     */
    public static void matchParticipants(Tournament tournament,
                                         MatchRepository matchRepository,
                                         List<Participant> participants,
                                         ParticipantRepository participantRepository) {
        int i = 0;
        int totalParticipants = participants.size();
        while (i < totalParticipants) {
            while (i < totalParticipants && !participants.get(i).isActive()) {
                ++i;
            }

            if (i == totalParticipants) {
                break;
            }

            int firstParticipantIndex = i++;

            while (i < totalParticipants && !participants.get(i).isActive()) {
                ++i;
            }

            if (i == totalParticipants) {
                break;
            }

            int secondParticipantIndex = i++;

            Participant firstParticipant = participants.get(firstParticipantIndex);
            Participant secondParticipant = participants.get(secondParticipantIndex);

            Match firstMatch = firstParticipant.getMatch();
            Match secondMatch = secondParticipant.getMatch();

            Match match = new Match();

            firstParticipant.setMatch(match);
            secondParticipant.setMatch(match);

            if (firstMatch.getStartTime() == null) {
                matchRepository.delete(firstMatch);
            }

            if (secondMatch.getStartTime() == null) {
                matchRepository.delete(secondMatch);
            }

            match.setStartTime(LocalDateTime.now());

            List<Participant> matchesParticipants = new ArrayList<>();
            matchesParticipants.add(firstParticipant);
            matchesParticipants.add(secondParticipant);
            match.setFirstParticipantId(firstParticipant.getId());
            match.setSecondParticipantId(secondParticipant.getId());
            match.setParticipants(matchesParticipants);
            match.setTournament(tournament);

            matchRepository.save(match);

            firstParticipant.setMatch(match);
            secondParticipant.setMatch(match);

            participantRepository.save(firstParticipant);
            participantRepository.save(secondParticipant);
        }

        int activeParticipants = 0;
        for (Participant participant : participants) {
            if (participant.isActive()) {
                activeParticipants += 1;
            }
        }

        Utils.setTotalMatches(activeParticipants / 2);
    }

    /**
     * Gets the number of total matches in the current layer.
     * @return the number of total matches.
     */
    public static Integer getTotalMatches() {
        return totalMatches;
    }

    /**
     * Updates the number of total matches in the current layer.
     *
     * @param totalMatches - the new value for total matches.
     */
    public static void setTotalMatches(Integer totalMatches) {
        Utils.totalMatches = totalMatches;
    }

    /**
     * Gets the number of already played matches in the current layer.
     *
     * @return the number of already played matches in the current layer.
     */
    public static Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    /**
     * Updates the number of already played matches in the current layer.
     *
     * @param matchesPlayed - the new value for already played matches.
     */
    public static void setMatchesPlayed(Integer matchesPlayed) {
        Utils.matchesPlayed = matchesPlayed;
    }
}
