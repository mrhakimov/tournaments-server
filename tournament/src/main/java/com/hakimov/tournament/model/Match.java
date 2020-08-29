package com.hakimov.tournament.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "match")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "match")
    private List<Participant> participants;

    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    private Long firstParticipantScore;
    private Long secondParticipantScore;

    @JsonIgnore
    private Long firstParticipantId;
    @JsonIgnore
    private Long secondParticipantId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public Long getFirstParticipantScore() {
        return firstParticipantScore;
    }

    public void setFirstParticipantScore(Long firstParticipantScore) {
        this.firstParticipantScore = firstParticipantScore;
    }

    public Long getSecondParticipantScore() {
        return secondParticipantScore;
    }

    public void setSecondParticipantScore(Long secondParticipantScore) {
        this.secondParticipantScore = secondParticipantScore;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Long getFirstParticipantId() {
        return firstParticipantId;
    }

    public void setFirstParticipantId(Long firstParticipantId) {
        this.firstParticipantId = firstParticipantId;
    }

    public Long getSecondParticipantId() {
        return secondParticipantId;
    }

    public void setSecondParticipantId(Long secondParticipantId) {
        this.secondParticipantId = secondParticipantId;
    }
}
