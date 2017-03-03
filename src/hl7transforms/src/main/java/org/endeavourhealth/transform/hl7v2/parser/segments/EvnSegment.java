package org.endeavourhealth.transform.hl7v2.parser.segments;

import org.endeavourhealth.transform.hl7v2.parser.ParseException;
import org.endeavourhealth.transform.hl7v2.parser.Segment;
import org.endeavourhealth.transform.hl7v2.parser.Seperators;
import org.endeavourhealth.transform.hl7v2.parser.datatypes.Xcn;
import org.endeavourhealth.transform.hl7v2.transform.Hl7DateTime;

import java.util.List;

public class EvnSegment extends Segment {
    public EvnSegment(String segment, Seperators seperators) throws ParseException {
        super(segment, seperators);
    }

    public String getEventTypeCode() { return this.getFieldAsString(1); }
    public Hl7DateTime getRecordedDateTime() throws ParseException { return this.getFieldAsHl7Date(2); }
    public Hl7DateTime getPlannedDateTime() throws ParseException { return this.getFieldAsHl7Date(3); }
    public String getEventReasonCode() { return this.getFieldAsString(4); }
    public List<Xcn> getOperators() { return this.getFieldAsDatatypes(5, Xcn.class); }
    public Hl7DateTime getOccurredDateTime() throws ParseException { return this.getFieldAsHl7Date(6); }
}
