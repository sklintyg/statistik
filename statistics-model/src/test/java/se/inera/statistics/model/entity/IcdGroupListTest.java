package se.inera.statistics.model.entity;

import static org.junit.Assert.assertSame;

import org.junit.Test;

public class IcdGroupListTest {

    
    private static final IcdGroup ICD_GROUP_A = new IcdGroup("Chapter A", "A00", "A99", "Chapter A ICD");
    private static final IcdGroup ICD_GROUP_B = new IcdGroup("Chapter B", "B00", "B99", "Chapter B ICD");
    private static final IcdGroup ICD_GROUP_C = new IcdGroup("Chapter C", "C00", "C99", "Chapter C ICD");
    
    private IcdGroupList groupList = new IcdGroupList();

    @Test
    public void emptyListReturnsUnknownIcd() {
        IcdGroup icdGroup = groupList.getGroup("anything");
        assertSame(IcdGroupList.UNKNOWN_ICD, icdGroup);
    }

    @Test
    public void matchCorrectIcd() {
        groupList.add(ICD_GROUP_A);
        groupList.add(ICD_GROUP_B);
        groupList.add(ICD_GROUP_C);
        
        IcdGroup icdGroup = groupList.getGroup("B12.3");
        assertSame(ICD_GROUP_B, icdGroup);
    }

    @Test
    public void nonMatchingIcdReturnsUnknownIcdIcd() {
        groupList.add(ICD_GROUP_A);
        groupList.add(ICD_GROUP_B);
        groupList.add(ICD_GROUP_C);
        
        IcdGroup icdGroup = groupList.getGroup("Z01");
        assertSame(IcdGroupList.UNKNOWN_ICD, icdGroup);
    }

}
