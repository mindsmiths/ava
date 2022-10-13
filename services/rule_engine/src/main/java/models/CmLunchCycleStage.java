package models;

public enum CmLunchCycleStage {
    COLLECT_AVA_AVAILABILITIES,
    GENERATE_MATCHES,
    COLLECT_GENERATED_MATCHES,
    SEND_TO_AVAS,
    FINISHED;
}
