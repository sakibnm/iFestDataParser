package com.example.ifestdataparser;

import java.util.ArrayList;

public class User {
    String email;
    ArrayList<Event> events;

    public User(String email, ArrayList<Event> events) {
        this.email = email;
        this.events = events;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", events=" + events +
                '}';
    }
}
