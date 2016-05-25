package com.xtuer.bean;

public class ParticipantGift {
    private int id;
    private Participant participant;
    private Gift gift;
    private String description;

    public ParticipantGift() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ParticipantGift{" +
                "id=" + id +
                ", participant=" + participant +
                ", gift=" + gift +
                ", description='" + description + '\'' +
                '}';
    }
}
