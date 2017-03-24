package org.endeavourhealth.hl7transform.homerton.transforms;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.endeavourhealth.common.fhir.FhirUri;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.common.fhir.schema.OrganisationType;
import org.endeavourhealth.hl7parser.ParseException;
import org.endeavourhealth.hl7transform.common.TransformException;
import org.endeavourhealth.hl7transform.homerton.transforms.converters.IdentifierConverter;
import org.endeavourhealth.hl7transform.mapper.Mapper;
import org.endeavourhealth.hl7transform.mapper.MapperException;
import org.endeavourhealth.hl7transform.common.ResourceContainer;
import org.endeavourhealth.hl7transform.common.converters.AddressConverter;
import org.endeavourhealth.hl7transform.common.converters.StringHelper;
import org.endeavourhealth.hl7transform.common.converters.TelecomConverter;
import org.hl7.fhir.instance.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class OrganizationTransform extends TransformBase {

    public static final String homertonOrganisationName = "Homerton University Hospital NHS Foundation Trust";
    public static final String homertonOdsCode = "RQX";
    public static final String homertonAddressLine = "Homerton Row";
    public static final String homertonCity = "London";
    public static final String homertonPostcode = "E9 6SR";

    public OrganizationTransform(Mapper mapper, ResourceContainer resourceContainer) {
        super(mapper, resourceContainer);
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.Organization;
    }

    public Reference createHomertonManagingOrganisation() throws MapperException, TransformException, ParseException {

        Organization organization = new Organization()
                .addIdentifier(IdentifierConverter.createOdsCodeIdentifier(homertonOdsCode))
                .setType(getOrganisationType(OrganisationType.NHS_TRUST))
                .setName(homertonOrganisationName)
                .addAddress(AddressConverter.createWorkAddress(Arrays.asList(homertonAddressLine), homertonCity, homertonPostcode));

        mapAndSetId(getUniqueIdentifyingString(homertonOdsCode, homertonOrganisationName), organization);

        targetResources.addManagingOrganisation(organization);

        return ReferenceHelper.createReference(ResourceType.Organization, organization.getId());
    }

    public Reference createHomertonHospitalServiceOrganisation(String hospitalServiceName, String servicingFacilityName) throws TransformException, ParseException, MapperException {
        if (StringUtils.isBlank(hospitalServiceName))
            return null;

        if (!StringUtils.trim(servicingFacilityName).toUpperCase().equals("HOMERTON UNIVER"))
            throw new TransformException("Hospital servicing facility of " + servicingFacilityName + " not recognised");

        Reference managingOrganisationReference = createHomertonManagingOrganisation();

        Organization organization = new Organization()
                .setName(hospitalServiceName)
                .setType(getOrganisationType(OrganisationType.NHS_TRUST_SERVICE))
                .addAddress(AddressConverter.createWorkAddress(Arrays.asList(homertonOrganisationName, homertonAddressLine), homertonCity, homertonPostcode))
                .setPartOf(managingOrganisationReference);

        mapAndSetId(getUniqueIdentifyingString(homertonOdsCode, homertonOrganisationName, hospitalServiceName), organization);

        return ReferenceHelper.createReference(ResourceType.Organization, organization.getId());
    }

    public Reference createGeneralPracticeOrganisation(String odsCode, String practiceName, List<String> addressLines, String city, String postcode, String phoneNumber) throws MapperException, TransformException, ParseException {

        if (StringUtils.isBlank(practiceName))
            return null;

        Organization organization = new Organization();

        mapAndSetId(getUniqueIdentifyingString(odsCode, practiceName), organization);

        Identifier identifier = IdentifierConverter.createOdsCodeIdentifier(odsCode);

        if (identifier != null)
            organization.addIdentifier(identifier);

        organization.setName(StringHelper.formatName(practiceName));

        if (StringUtils.isNotBlank(phoneNumber))
            organization.addTelecom(TelecomConverter.createWorkPhone(phoneNumber));

        Address address = AddressConverter.createWorkAddress(addressLines, city, postcode);

        if (address != null)
            organization.addAddress(address);

        organization.setType(getOrganisationType(OrganisationType.GP_PRACTICE));

        targetResources.addResource(organization);

        return ReferenceHelper.createReference(ResourceType.Organization, organization.getId());
    }

    private CodeableConcept getOrganisationType(OrganisationType organisationType) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem(organisationType.getSystem())
                        .setDisplay(organisationType.getDescription())
                        .setCode(organisationType.getCode()));
    }

    private String getUniqueIdentifyingString(String odsCode, String name) {
        Validate.notBlank(odsCode, "odsCode");
        Validate.notBlank(name, "name");

        return createIdentifyingString(ImmutableMap.of(
                "OdsCode", odsCode,
                "Name", name
        ));
    }

    private String getUniqueIdentifyingString(String parentOdsCode, String parentName, String serviceName) {
        Validate.notBlank(parentOdsCode, "parentOdsCode");
        Validate.notBlank(parentName, "parentName");
        Validate.notBlank(serviceName, "serviceName");

        return createIdentifyingString(ImmutableMap.of(
                "ParentOdsCode", parentOdsCode,
                "ParentName", parentName,
                "ServiceName", serviceName
        ));
    }
}
