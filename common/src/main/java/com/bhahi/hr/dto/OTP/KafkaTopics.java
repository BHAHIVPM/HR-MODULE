package com.bhahi.hr.dto.OTP;

/**
 * Kafka names shared by the sender (hrmodule) and the receiver (hrmod).
 */
public final class KafkaTopics {

    private KafkaTopics() {
    }

    /**
     * One topic for every event sent from hrmodule to hrmod.
     * The kind of event is told by the EVENT_TYPE_HEADER, see KafkaEventType.
     * (A single topic keeps you far from the 5 topic limit of the Aiven plan.)
     */
    public static final String HR_EVENTS = "hr-events";

    /**
     * Name of the Kafka record header that holds the KafkaEventType.
     */
    public static final String EVENT_TYPE_HEADER = "eventType";
}