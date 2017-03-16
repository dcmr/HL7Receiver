package org.endeavourhealth.transform.hl7v2.profiles.homerton.transforms;

import org.endeavourhealth.hl7parser.ParseException;
import org.endeavourhealth.hl7parser.datatypes.Xcn;
import org.endeavourhealth.transform.hl7v2.profiles.homerton.segments.ZpiSegment;
import org.endeavourhealth.transform.hl7v2.transform.TransformException;
import org.endeavourhealth.transform.hl7v2.transform.converters.AddressConverter;
import org.endeavourhealth.transform.hl7v2.transform.converters.NameConverter;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Reference;

public class AdditionalPatientInfoTransform {

    public static void addAdditionalInformation(Patient target, ZpiSegment source) throws TransformException, ParseException {

        if (source.getPatientTemporaryAddress() != null)
            for (Address address : AddressConverter.convert(source.getPatientTemporaryAddress()))
                target.addAddress(address);

        if (source.getOtherProvider() != null) {
            for (Xcn xcn : source.getOtherProvider()) {

                Reference reference = new Reference()
                        .setDisplay(NameConverter.getNameAsString(xcn))
                        .setReference(xcn.getId());

                target.addCareProvider(reference);
            }
        }
    }
}
