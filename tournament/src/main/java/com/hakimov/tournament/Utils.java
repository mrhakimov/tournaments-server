package com.hakimov.tournament;

import com.hakimov.tournament.model.Match;
import com.hakimov.tournament.model.Participant;
import com.hakimov.tournament.model.Tournament;
import com.hakimov.tournament.repositories.MatchRepository;
import com.hakimov.tournament.repositories.ParticipantRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static Integer totalMatches;
    private static Integer matchesPlayed = 0;
    
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
                                         List<Participant> participants,
                                         ParticipantRepository participantRepository) {
        int i = 0;
        int totalParticipants = participants.size();
        System.out.println("================");
        System.out.println(totalParticipants);
        while (i < totalParticipants) {
            while (i < totalParticipants && participants.get(i).isNotActive()) {
                ++i;
            }

            if (i == totalParticipants) {
                break;
            }

            int firstParticipantIndex = i++;

            while (i < totalParticipants && participants.get(i).isNotActive()) {
                ++i;
            }

            if (i == totalParticipants) {
                break;
            }

            int secondParticipantIndex = i++;

            Participant firstParticipant = participants.get(firstParticipantIndex);
            Participant secondParticipant = participants.get(secondParticipantIndex);

            System.out.println("" + firstParticipantIndex + ", " + secondParticipantIndex + " => "
            + firstParticipant.getNickname() + ", " + secondParticipant.getNickname());

            Match match = firstParticipant.getMatch();
            Match matchToDelete = secondParticipant.getMatch();

            secondParticipant.setMatch(match);

//            if (matchToDelete != null) {
//                matchRepository.delete(matchToDelete);
//            }

            match.setStartTime(LocalDateTime.now());

            List<Participant> matchesParticipants = new ArrayList<>();
            matchesParticipants.add(firstParticipant);
            matchesParticipants.add(secondParticipant);
            match.setParticipants(matchesParticipants);
            match.setTournament(tournament);

            matchRepository.save(match);

            firstParticipant.setMatch(match);
            secondParticipant.setMatch(match);

            participantRepository.save(firstParticipant);
            participantRepository.save(secondParticipant);

            System.out.println("DONE ---------------------------");
        }

        int activeParticipants = 0;
        for (int j = 0; j < participants.size(); ++j) {
            if (participants.get(j).isActive()) {
                activeParticipants += 1;
            }
        }

        Utils.setTotalMatches(activeParticipants / 2);
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
