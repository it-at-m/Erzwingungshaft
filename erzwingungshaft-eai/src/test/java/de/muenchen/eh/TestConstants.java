package de.muenchen.eh;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestConstants {

    public static final String SPRING_TEST_PROFILE = "test";
    public static final String SPRING_INTEGRATION_PROFILE = "integration";
    public static final String SPRING_DEVELOPMENT_PROFILE = "development";

}
