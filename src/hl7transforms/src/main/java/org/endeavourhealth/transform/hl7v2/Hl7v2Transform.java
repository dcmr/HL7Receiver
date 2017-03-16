package org.endeavourhealth.transform.hl7v2;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.common.fhir.JsonHelper;
import org.endeavourhealth.transform.hl7v2.mapper.Mapper;
import org.endeavourhealth.hl7parser.Message;
import org.endeavourhealth.hl7parser.ParseException;
import org.endeavourhealth.hl7parser.messages.AdtMessage;
import org.endeavourhealth.hl7parser.segments.MshSegment;
import org.endeavourhealth.hl7parser.segments.SegmentName;
import org.endeavourhealth.transform.hl7v2.profiles.DefaultTransformProfile;
import org.endeavourhealth.transform.hl7v2.profiles.TransformProfile;
import org.endeavourhealth.transform.hl7v2.profiles.homerton.HomertonTransformProfile;
import org.endeavourhealth.transform.hl7v2.transform.AdtMessageTransform;
import org.endeavourhealth.transform.hl7v2.transform.TransformException;
import org.hl7.fhir.instance.model.Bundle;

public class Hl7v2Transform {
    public static String transform(String message, Mapper mapper) throws Exception {

        /////
        ///// get the sending facility and calculate the transform profile
        /////
        String sendingFacility = getSendingFacility(message);
        TransformProfile transformProfile = getTransformProfile(sendingFacility);

        /////
        ///// construct our message with including the profile's Z segments
        /////
        AdtMessage adtMessage = new AdtMessage(message, transformProfile.getZSegments());

        /////
        ///// perform any pre transform activities defined by the profile
        /////
        adtMessage = transformProfile.preTransform(adtMessage);

        /////
        ///// perform the actual transform and output as JSON
        /////
        Bundle bundle = AdtMessageTransform.transform(adtMessage, transformProfile, mapper);
        return JsonHelper.getPrettyJson(bundle);
    }

    public static String preTransformOnly(String message) throws Exception {
        String sendingFacility = getSendingFacility(message);

        TransformProfile transformProfile = getTransformProfile(sendingFacility);

        AdtMessage adtMessage = new AdtMessage(message, transformProfile.getZSegments());

        adtMessage = transformProfile.preTransform(adtMessage);

        return adtMessage.compose();
    }

    public static String parseAndRecompose(String message) throws Exception {
        AdtMessage sourceMessage = new AdtMessage(message);

        return sourceMessage.compose();
    }

    private static TransformProfile getTransformProfile(String sendingFacility) {
        switch (sendingFacility) {
            case "HOMERTON": return new HomertonTransformProfile();
            default: return new DefaultTransformProfile();
        }
    }

    private static String getSendingFacility(String message) throws ParseException, TransformException {
        Message parsedMessage = new Message(message);
        MshSegment mshSegment = parsedMessage.getSegment(SegmentName.MSH, MshSegment.class);

        if (mshSegment == null)
            throw new TransformException("MSH segment not found");

        if (StringUtils.isBlank(mshSegment.getSendingFacility()))
            throw new TransformException("Sending facility is blank");

        return mshSegment.getSendingFacility();
    }
}
