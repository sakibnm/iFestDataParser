package com.example.ifestdataparser;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Event {
    String description;
    String time;

    public Event(){

    }

    public Event(String description, String time) {
        this.description = description;
        this.time = time;
    }

    public Event(Map eventsMap){
        this.description = (String) eventsMap.get("description");
        this.time = (String) eventsMap.get("time");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map toHashMap(){
        Map<String, Object> eventsMap = new HashMap<>();

        eventsMap.put("description", this.description);
        eventsMap.put("time", this.time);

        return eventsMap;
    }

    @Override
    public String toString() {
        return "Event{" +
                "description='" + description + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
