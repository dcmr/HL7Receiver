package org.endeavourhealth.hl7transform.homerton.transforms;

import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.common.fhir.FhirUri;
import org.endeavourhealth.common.fhir.ReferenceHelper;
import org.endeavourhealth.hl7parser.Helpers;
import org.endeavourhealth.hl7transform.common.TransformException;
import org.endeavourhealth.hl7transform.common.converters.AddressConverter;
import org.endeavourhealth.hl7transform.mapper.Mapper;
import org.endeavourhealth.hl7transform.mapper.MapperException;
import org.endeavourhealth.hl7transform.common.ResourceContainer;
import org.endeavourhealth.hl7parser.datatypes.Pl;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.valuesets.LocationPhysicalType;
import org.hl7.fhir.instance.model.valuesets.V3RoleCode;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import java.util.*;
import java.util.stream.Collectors;

public class LocationTransform {

    private Mapper mapper;
    private ResourceContainer targetResources;

    public LocationTransform(Mapper mapper, ResourceContainer targetResources) {
        this.mapper = mapper;
        this.targetResources = targetResources;
    }

    public Reference createHomertonLocation() throws MapperException, TransformException {

        final String odsCode = "RQXM1";
        final String locationName = "Homerton University Hospital";

        Location location = new Location()
                .setName(locationName)
                .addIdentifier(new Identifier()
                        .setSystem(FhirUri.IDENTIFIER_SYSTEM_ODS_CODE)
                        .setValue(odsCode))
                .setStatus(Location.LocationStatus.ACTIVE)
                .setAddress(AddressConverter.createWorkAddress(Arrays.asList("Homerton Row"), "London", "E9 6SR"))
                .setManagingOrganization(this.targetResources.getManagingOrganisation())
                .setType(new CodeableConcept()
                        .addCoding(new Coding()
                                .setCode(V3RoleCode.HOSP.toCode())
                                .setDisplay(V3RoleCode.HOSP.getDisplay())
                                .setSystem(V3RoleCode.HOSP.getSystem())))
                .setPhysicalType(createLocationPhysicalType(LocationPhysicalType.BU))
                .setMode(Location.LocationMode.INSTANCE);

        UUID id = getId(odsCode, locationName, mapper);
        location.setId(id.toString());

        targetResources.addManagingLocation(location);

        return ReferenceHelper.createReference(ResourceType.Location, location.getId());
    }

    public Reference transformAndGetReference(Pl source) throws MapperException, TransformException {

        List<Pair<LocationPhysicalType, String>> locations = new ArrayList<>();

        locations.add(new Pair<>(LocationPhysicalType.BU, source.getBuilding()));
        locations.add(new Pair<>(LocationPhysicalType.WI, source.getPointOfCare()));
        locations.add(new Pair<>(LocationPhysicalType.RO, source.getRoom()));
        locations.add(new Pair<>(LocationPhysicalType.BD, source.getBed()));

        String facility = StringUtils.trim(StringUtils.defaultString(source.getFacility())).toLowerCase();

        Reference managingOrganisation = null;

        if (facility.equals("homerton univer") || facility.equals("homerton uh"))
            managingOrganisation = targetResources.getManagingOrganisation();

        locations = locations
                .stream()
                .filter(t -> StringUtils.isNotBlank(t.getValue()))
                .collect(Collectors.toList());

        Location lastLocation = null;

        for (int i = 1; i <= locations.size(); i++) {
            List<Pair<LocationPhysicalType, String>> location = Lists.reverse(locations
                    .stream()
                    .limit(i)
                    .collect(Collectors.toList()));

            Location fhirLocation = createLocation(location, lastLocation, managingOrganisation, mapper);

            if (fhirLocation != null)
                targetResources.addResource(fhirLocation);

            lastLocation = fhirLocation;
        }

        if (lastLocation == null)
            return null;

        return ReferenceHelper.createReference(ResourceType.Location, lastLocation.getId());
    }

    private static Location createLocation(List<Pair<LocationPhysicalType, String>> locations,
                                           Location parentLocation,
                                           Reference managingOrganisation,
                                           Mapper mapper) throws MapperException {
        if (locations.size() == 0)
            return null;

        String[] locationsNames = locations
                .stream()
                .map(t -> t.getValue())
                .collect(Collectors.toList())
                .toArray(new String[locations.size()]);

        String locationName = String.join(", ", locationsNames);
        UUID id = getId(locationsNames, mapper);

        Location location = new Location();

        location.setId(id.toString());
        location.setName(locationName);
        location.setMode(Location.LocationMode.INSTANCE);
        location.setPhysicalType(createLocationPhysicalType(locations.get(0).getKey()));

        if (managingOrganisation != null)
            location.setManagingOrganization(managingOrganisation);

        if (parentLocation != null)
            location.setPartOf(ReferenceHelper.createReference(ResourceType.Location, parentLocation.getId()));

        return location;
    }

    private static UUID getId(String odsCode, String locationName, Mapper mapper) throws MapperException {

        odsCode = StringUtils.upperCase(StringUtils.deleteWhitespace(odsCode));
        locationName = StringUtils.remove(StringUtils.upperCase(StringUtils.deleteWhitespace(locationName).toUpperCase()), ".");

        String uniqueIdentifyingString = "Location-OdsCode=" + odsCode + "-" + locationName;
        return mapper.mapResourceUuid(ResourceType.Location, uniqueIdentifyingString);
    }

    private static UUID getId(String[] locationNames, Mapper mapper) throws MapperException {
        return getId("", StringUtils.join(locationNames, "-"), mapper);
    }

    private static CodeableConcept createLocationPhysicalType(LocationPhysicalType locationPhysicalType) {
        return new CodeableConcept()
                .addCoding(
                        new Coding()
                                .setCode(locationPhysicalType.toCode())
                                .setSystem(locationPhysicalType.getSystem())
                                .setDisplay(locationPhysicalType.getDisplay()));
    }
}
