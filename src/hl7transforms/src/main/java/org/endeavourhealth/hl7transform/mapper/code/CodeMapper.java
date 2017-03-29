package org.endeavourhealth.hl7transform.mapper.code;

import org.endeavourhealth.hl7transform.homerton.transforms.valuesets.HomertonAdmissionType;
import org.endeavourhealth.hl7transform.homerton.transforms.valuesets.HomertonDischargeDisposition;
import org.endeavourhealth.hl7transform.homerton.transforms.valuesets.HomertonEncounterType;
import org.endeavourhealth.hl7transform.mapper.Mapper;
import org.endeavourhealth.hl7transform.mapper.exceptions.MapperException;
import org.hl7.fhir.instance.model.*;

public class CodeMapper extends CodeMapperBase {

    public CodeMapper(Mapper mapper) {
        super(mapper);
    }

    public Enumerations.AdministrativeGender mapSex(String sex) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_SEX,
                        sex,
                        (t) -> Enumerations.AdministrativeGender.fromCode(t),
                        (r) -> r.getSystem());
    }

    public HumanName.NameUse mapNameType(String nameType) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_NAME_TYPE,
                        nameType,
                        (t) -> HumanName.NameUse.fromCode(t),
                        (r) -> r.getSystem());
    }

    public Address.AddressUse mapAddressType(String addressType) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_ADDRESS_TYPE,
                        addressType,
                        (t) -> Address.AddressUse.fromCode(t),
                        (r) -> r.getSystem());
    }

    public ContactPoint.ContactPointSystem mapTelecomEquipmentType(String telecomEquipmentType) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_TELECOM_EQUIPMENT_TYPE,
                        telecomEquipmentType,
                        (t) -> ContactPoint.ContactPointSystem.fromCode(t),
                        (r) -> r.getSystem());
    }

    public ContactPoint.ContactPointUse mapTelecomUse(String telecomUse) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_TELECOM_USE,
                        telecomUse,
                        (t) -> ContactPoint.ContactPointUse.fromCode(t),
                        (r) -> r.getSystem());
    }

    public Encounter.EncounterState mapAccountStatus(String accountStatus) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_ACCOUNT_STATUS,
                        accountStatus,
                        (t) -> Encounter.EncounterState.fromCode(t),
                        (r) -> r.getSystem());
    }

    public Encounter.EncounterClass mapPatientClass(String patientClass) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_PATIENT_CLASS,
                        patientClass,
                        (t) -> Encounter.EncounterClass.fromCode(t),
                        (r) -> r.getSystem());
    }

    public HomertonEncounterType mapPatientType(String patientType) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_PATIENT_TYPE,
                        patientType,
                        (t) -> HomertonEncounterType.fromCode(t),
                        (r) -> r.getSystem());
    }

    public HomertonAdmissionType mapAdmissionType(String admissionType) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_ADMISSION_TYPE,
                        admissionType,
                        (t) -> HomertonAdmissionType.fromCode(t),
                        (r) -> r.getSystem());
    }

    public HomertonDischargeDisposition mapDischargeDisposition(String dischargeDisposition) throws MapperException {
        return this
                .mapCodeToEnum(
                        CodeContext.HL7_DISCHARGE_DISPOSITION,
                        dischargeDisposition,
                        (t) -> HomertonDischargeDisposition.fromCode(t),
                        (r) -> r.getSystem());
    }


}
