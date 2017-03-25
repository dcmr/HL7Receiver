package org.endeavourhealth.hl7transform.homerton.transforms.valuesets;

import org.apache.commons.lang3.StringUtils;
import org.endeavourhealth.hl7transform.common.TransformException;
import org.hl7.fhir.instance.model.Encounter;

public abstract class EncounterStateVs {

    public static Encounter.EncounterState convert(String state) throws TransformException {
        state = StringUtils.defaultString(state).trim().toUpperCase();

        switch (state) {
            case "PREADMIT": return Encounter.EncounterState.PLANNED;
            case "ACTIVE": return Encounter.EncounterState.INPROGRESS;
            case "DISCHARGED": return Encounter.EncounterState.FINISHED;
            case "CANCELLED": return Encounter.EncounterState.CANCELLED;

            default: throw new TransformException(state + " encounter state not recognised");
        }
    }
}
