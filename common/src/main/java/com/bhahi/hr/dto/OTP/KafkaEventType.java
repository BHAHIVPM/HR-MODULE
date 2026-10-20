package com.bhahi.hr.dto.OTP;

/**
 * Every kind of event that hrmodule can send to hrmod.
 * To add a new purpose: add a constant here, create its DTO in common,
 * publish it from hrmodule, and add one case in the hrmod consumer.
 */
public enum KafkaEventType {
    OTP_MAIL
}