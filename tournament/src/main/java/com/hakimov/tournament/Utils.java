package com.hakimov.tournament;

import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static Integer totalMatches;
    private static Integer matchesPlayed;
    
    public static String tournamentNotFoundMessage(Long id) {
        return "Can't find tournament with id: " + id + "!";
    }

    public static String participantNotFoundMessage() {
        return "Can't find participant!";
    }

    public static String matchNotFoundMessage(Long id) {
        return "Can't find match with id: " + id + "!";
    }

    public static void matchParticipants(Tournament tournament,
                                         MatchRepository matchRepository,
                                         List<Participant> participants) {
        int i = 0;
        int totalParticipants = participants.size();
        while (i < totalParticipants) {
            while (i < totalParticipants && participants.get(i).isNotActive()) {
                ++i;
            }

            if (i == totalParticipants) {
                break;
            }

            int firstParticipant = i++;

            while (i < totalParticipants && participants.get(i).isNotActive()) {
                ++i;
            }

            if (i == totalParticipants) {
                break;
            }

            int secondParticipant = i;

            Match match = new Match();
            match.setStartTime(LocalDateTime.now());

            List<Participant> matchesParticipants = new ArrayList<>();
            matchesParticipants.add(participants.get(firstParticipant));
            matchesParticipants.add(participants.get(secondParticipant));
            match.setParticipants(matchesParticipants);
            match.setTournament(tournament);

            matchRepository.save(match);
        }

        Utils.setTotalMatches(totalParticipants / 2 + (totalParticipants / 2 % 2) * (totalParticipants % 2));
    }

    public static Integer getTotalMatches() {
        return totalMatches;
    }

    public static void setTotalMatches(Integer totalMatches) {
        Utils.totalMatches = totalMatches;
    }

    public static Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public static void setMatchesPlayed(Integer matchesPlayed) {
        Utils.matchesPlayed = matchesPlayed;
    }
}
