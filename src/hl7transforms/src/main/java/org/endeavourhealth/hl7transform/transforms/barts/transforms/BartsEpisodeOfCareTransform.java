package org.endeavourhealth.hl7transform.transforms.barts.transforms;

import org.endeavourhealth.hl7parser.Hl7DateTime;
import org.endeavourhealth.hl7parser.ParseException;
import org.endeavourhealth.hl7parser.datatypes.Cx;
import org.endeavourhealth.hl7parser.messages.AdtMessage;
import org.endeavourhealth.hl7parser.segments.EvnSegment;
import org.endeavourhealth.hl7parser.segments.MrgSegment;
import org.endeavourhealth.hl7parser.segments.Pv1Segment;
import org.endeavourhealth.hl7transform.common.ResourceContainer;
import org.endeavourhealth.hl7transform.common.ResourceTag;
import org.endeavourhealth.hl7transform.common.ResourceTransformBase;
import org.endeavourhealth.hl7transform.common.TransformException;
import org.endeavourhealth.hl7transform.common.converters.DateTimeHelper;
import org.endeavourhealth.hl7transform.common.converters.IdentifierConverter;
import org.endeavourhealth.hl7transform.common.transform.EpisodeOfCareCommon;
import org.endeavourhealth.hl7transform.mapper.Mapper;
import org.endeavourhealth.hl7transform.mapper.exceptions.MapperException;
import org.endeavourhealth.hl7transform.mapper.resource.MappedResourceUuid;
import org.endeavourhealth.hl7transform.transforms.barts.constants.BartsConstants;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BartsEpisodeOfCareTransform extends ResourceTransformBase {

    private static final Logger LOG = LoggerFactory.getLogger(BartsEpisodeOfCareTransform.class);

    public BartsEpisodeOfCareTransform(Mapper mapper, ResourceContainer targetResources) {
        super(mapper, targetResources);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.EpisodeOfCare;
    }

    public EpisodeOfCare transform(AdtMessage source) throws TransformException, MapperException, ParseException {

        if (!source.hasPv1Segment())
            return null;

        EpisodeOfCare target = new EpisodeOfCare();

        setId(source, target);

        setIdentifiers(source, target);

        setPatient(target);

        setManagingOrganisation(source, target);

        setStatusAndPeriod(source, target);

        return target;
    }

    protected void setId(AdtMessage source, EpisodeOfCare target) throws TransformException, MapperException {
        UUID episodeUuid = getBartsMappedEpisodeOfCareUuid(source, mapper);
        target.setId(episodeUuid.toString());
    }

    public static String getBartsPrimaryEpisodeIdentifierValue(AdtMessage source) {
        return EpisodeOfCareCommon.getEpisodeIdentifierValueByTypeCode(source, BartsConstants.primaryEpisodeIdentifierTypeCode);
    }

    public static String getBartsPrimaryEpisodeIdentifierValue(MrgSegment source) {
        return EpisodeOfCareCommon.getEpisodeIdentifierValueByTypeCode(Arrays.asList(source.getPriorVisitNumber()), BartsConstants.primaryEpisodeIdentifierTypeCode);
    }

    public static UUID getBartsMappedEpisodeOfCareUuid(AdtMessage source, Mapper mapper) throws MapperException {
        String patientIdentifierValue = BartsPatientTransform.getBartsPrimaryPatientIdentifierValue(source);
        String episodeIdentifierValue = getBartsPrimaryEpisodeIdentifierValue(source);

        return getBartsMappedEpisodeOfCareUuid(patientIdentifierValue, episodeIdentifierValue, mapper);
    }

    public static UUID getBartsMappedEpisodeOfCareUuid(MrgSegment source, Mapper mapper) throws MapperException, ParseException {
        String patientIdentifierValue = BartsPatientTransform.getBartsPrimaryPatientIdentifierValue(source);
        String episodeIdentifierValue = getBartsPrimaryEpisodeIdentifierValue(source);

        return getBartsMappedEpisodeOfCareUuid(patientIdentifierValue, episodeIdentifierValue, mapper);
    }

    private static UUID getBartsMappedEpisodeOfCareUuid(String patientIdentifierValue, String episodeIdentifierValue, Mapper mapper) throws MapperException {
        return mapper.getResourceMapper().mapEpisodeUuid(
                null,
                BartsConstants.primaryPatientIdentifierAssigningAuthority,
                patientIdentifierValue,
                BartsConstants.primaryEpisodeIdentifierTypeCode,
                null,
                episodeIdentifierValue);
    }

    public static List<MappedResourceUuid> getBartsPatientResourceUuidMappings(MrgSegment mrgSegment, Mapper mapper) throws MapperException, ParseException {
        String patientIdentifierValue = BartsPatientTransform.getBartsPrimaryPatientIdentifierValue(mrgSegment);
        String episodeIdentifierValue = getBartsPrimaryEpisodeIdentifierValue(mrgSegment);

        return mapper.getResourceMapper().getEpisodeResourceUuidMappings(
                null,
                BartsConstants.primaryPatientIdentifierAssigningAuthority,
                patientIdentifierValue,
                BartsConstants.primaryEpisodeIdentifierTypeCode,
                null,
                episodeIdentifierValue);
    }

    private void setIdentifiers(AdtMessage source, EpisodeOfCare target) throws TransformException, MapperException {
        List<Cx> cxs = EpisodeOfCareCommon.getAllEpisodeIdentifiers(source);

        for (Cx cx : cxs) {
            Identifier episodeIdentifier = IdentifierConverter.createIdentifier(cx, getResourceType(), mapper);

            if (episodeIdentifier != null)
                target.addIdentifier(episodeIdentifier);
        }
    }

    private void setPatient(EpisodeOfCare target) throws TransformException {
        target.setPatient(targetResources.getResourceReference(ResourceTag.PatientSubject, Patient.class));
    }

    private void setManagingOrganisation(AdtMessage source, EpisodeOfCare target) throws TransformException {
        target.setManagingOrganization(targetResources.getResourceReference(ResourceTag.MainHospitalOrganisation, Organization.class));
    }

    private void setStatusAndPeriod(AdtMessage source, EpisodeOfCare target) throws TransformException, ParseException, MapperException {

        Pv1Segment pv1Segment = source.getPv1Segment();

        String accountStatus = pv1Segment.getAccountStatus();
        Hl7DateTime admitDate = pv1Segment.getAdmitDateTime();
        Hl7DateTime dischargeDate = pv1Segment.getDischargeDateTime();
        Hl7DateTime eventRecordedDate = source.getEvnSegment().getRecordedDateTime();

        EpisodeOfCareCommon.setStatusAndPeriod(target, accountStatus, admitDate, dischargeDate, eventRecordedDate, mapper);
    }
}
