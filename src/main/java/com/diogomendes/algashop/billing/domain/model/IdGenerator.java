package com.diogomendes.algashop.billing.domain.model;

import com.fasterxml.uuid.impl.TimeBasedEpochRandomGenerator;

import java.util.UUID;

import static com.fasterxml.uuid.Generators.timeBasedEpochRandomGenerator;

public class IdGenerator {

    private static final TimeBasedEpochRandomGenerator timeBasedEpochRandomGenerator
            = timeBasedEpochRandomGenerator();


    private IdGenerator() {
    }

    public static UUID generateTimeBasedUUID() {
        return timeBasedEpochRandomGenerator.generate();
    }

}
