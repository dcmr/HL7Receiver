package org.endeavourhealth.transform.hl7v2.parser.segments;

import org.endeavourhealth.transform.hl7v2.parser.ParseException;
import org.endeavourhealth.transform.hl7v2.parser.Segment;
import org.endeavourhealth.transform.hl7v2.parser.Seperators;
import org.endeavourhealth.transform.hl7v2.parser.datatypes.Ce;

public class NteSegment extends Segment {
    public NteSegment(String segmentText, Seperators seperators) throws ParseException {
        super(segmentText, seperators);
    }

    public Integer getSetId() throws ParseException { return this.getFieldAsInteger(1); }
    public String getSourceOfComment() { return this.getFieldAsString(2); }
    public String getComment() { return this.getFieldAsString(3); }
    public Ce getCommentType() { return this.getFieldAsDatatype(4, Ce.class); }
}
