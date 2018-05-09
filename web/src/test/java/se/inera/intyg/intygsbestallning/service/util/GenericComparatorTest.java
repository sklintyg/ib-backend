package se.inera.intyg.intygsbestallning.service.util;

import org.junit.Test;
import se.inera.intyg.intygsbestallning.service.stateresolver.UtredningStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class GenericComparatorTest {

    @Test
    public void testSortWithNullsAsc() {
        List<TestObject> list = buildStandardList();

        List<TestObject> sortedList = list.stream().sorted((o1, o2) -> GenericComparator.compare(TestObject.class, o1, o2, "stringLabel",
                true)).collect(Collectors.toList());
        assertEquals("A", sortedList.get(0).getStringLabel());
        assertEquals("B", sortedList.get(1).getStringLabel());
        assertEquals(null, sortedList.get(2).getStringLabel());
    }

    @Test
    public void testSortWithNullsDesc() {
        List<TestObject> list = buildStandardList();

        List<TestObject> sortedList = list.stream().sorted((o1, o2) -> GenericComparator.compare(TestObject.class, o1, o2, "stringLabel",
                false)).collect(Collectors.toList());
        assertEquals(null, sortedList.get(0).getStringLabel());
        assertEquals("B", sortedList.get(1).getStringLabel());
        assertEquals("A", sortedList.get(2).getStringLabel());
    }

    @Test
    public void testSortWithEnumAsc() {
        List<TestObject> list = buildStandardList();

        List<TestObject> sortedList = list.stream().sorted((o1, o2) -> GenericComparator.compare(TestObject.class, o1, o2, "utredningStatus",
                true)).collect(Collectors.toList());
        assertEquals(UtredningStatus.AVBRUTEN, sortedList.get(0).getUtredningStatus());
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, sortedList.get(1).getUtredningStatus());
        assertEquals(UtredningStatus.REDOVISA_TOLK, sortedList.get(2).getUtredningStatus());
    }

    @Test
    public void testSortWithEnumDesc() {
        List<TestObject> list = buildStandardList();

        List<TestObject> sortedList = list.stream().sorted((o1, o2) -> GenericComparator.compare(TestObject.class, o1, o2, "utredningStatus",
                false)).collect(Collectors.toList());
        assertEquals(UtredningStatus.REDOVISA_TOLK, sortedList.get(0).getUtredningStatus());
        assertEquals(UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK, sortedList.get(1).getUtredningStatus());
        assertEquals(UtredningStatus.AVBRUTEN, sortedList.get(2).getUtredningStatus());
    }

    private List<TestObject> buildStandardList() {
        return Arrays.asList(new TestObject("A", UtredningStatus.AVBRUTEN),
                new TestObject("B", UtredningStatus.HANDLINGAR_MOTTAGNA_BOKA_BESOK),
                new TestObject(null, UtredningStatus.REDOVISA_TOLK)
        );
    }


    private class TestObject {
        private String stringLabel;
        private UtredningStatus utredningStatus;

        public TestObject(String stringLabel, UtredningStatus utredningStatus) {
            this.stringLabel = stringLabel;
            this.utredningStatus = utredningStatus;
        }

        public String getStringLabel() {
            return stringLabel;
        }

        public void setStringLabel(String stringLabel) {
            this.stringLabel = stringLabel;
        }

        public UtredningStatus getUtredningStatus() {
            return utredningStatus;
        }

        public void setUtredningStatus(UtredningStatus utredningStatus) {
            this.utredningStatus = utredningStatus;
        }
    }

}
