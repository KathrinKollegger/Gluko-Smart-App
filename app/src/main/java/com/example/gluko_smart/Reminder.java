package com.example.gluko_smart;

public class Reminder {

        private int id;
        private String name;
        private int hour;
        private int minute;

        public Reminder(int id, String name, int hour, int minute) {
            this.id = id;
            this.name = name;
            this.hour = hour;
            this.minute = minute;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }
}

