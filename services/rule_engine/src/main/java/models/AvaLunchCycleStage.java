package models;
 
public enum AvaLunchCycleStage {
    LUNCH_MAIL_SENDING,
    FIND_AVAILABILITY,
    FORWARD_AVAILABILITY,
    ACCEPT_MATCH_INFO, 
    COLLECTED_ENOUGH_INFO,
    GUESSING_QUIZ,
    END_GUESSING;
}
