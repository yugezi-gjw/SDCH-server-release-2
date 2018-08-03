package com.varian.oiscn.patient.resource;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by gbt1220 on 1/13/2017.
 */
public class DefaultAriaIDGeneratorTest {

    private DefaultAriaIDGenerator generator;

    @Before
    public void setup() {
        generator = new DefaultAriaIDGenerator();
    }

    @Test
    public void givenWhenCallGenerateThenReturnNewAriaId() {
        assertThat(generator.generate(), not(""));
    }
}
