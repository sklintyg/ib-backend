package se.inera.intyg.intygsbestallning.service.util;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class EasterCalculatorTest {

    @Test
    public void test2018() {
        LocalDate localDate = EasterCalculator.easterDate(2018);
        assertEquals(1, localDate.getDayOfMonth());
        assertEquals(4, localDate.getMonthValue());
    }
}
