package models;

public enum IceBreakerStage {
    REQUEST_SENT,
    SEND_EMAIL,
    CALCULATE_LUNCH_DATE,
    SHOW_QUESTION,
    WAIT_FOR_ANSWER,
    FINISHED;
}