package com.example.gluko_smart;


import android.util.Log;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Reference;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FirebaseToFHIR {

    //Instanz FhirContext
    FhirContext ctx = FhirContext.forDstu3();

    //Methode zur Erstellung einer Observation
    public void createObservation(GlucoseValues glucoseValues) {
        //Observation-Ressoruce erstellen
        Observation observation = new Observation();

        //ID der Observation setzen mit aktueller Uhrzeit wos hochladen
        observation.setId("Blutgluckose Selbstmessung" + System.currentTimeMillis());

        //Werte der Observation setzen (Blutzuckerwert)
        Quantity glucoseQuantity = new Quantity();
        glucoseQuantity.setValue(glucoseValues.getBzWert());
        glucoseQuantity.setUnit("mg/dL");
        observation.setValue(glucoseQuantity);

        //Datum und Uhrzeit der Messung und
        // zuvor in ein DateTimeDt objekt konvertieren
        String dateString = glucoseValues.getTimestamp();
       // Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dateString);
      //  DateTimeDt effectiveDateTime = new DateTimeDt(date);
       // observation.setEffective(effectiveDateTime);

        // Patienten-Referenz setzen (Patient, zu dem die Observation gehört)
        Reference patientReference = new Reference();
        patientReference.setReference("Patient/7496229");
        observation.setSubject(patientReference);

        //Code hinzufügen mit loinc Basis
        //Code: 2339-0
        //Darstellung:
        //mit setCode wird er dann der Observation zugewiesen
        CodeableConcept code = new CodeableConcept();
        code.addCoding()
                .setSystem("http://loinc.org")
                .setCode("2339-0")
                .setDisplay("Glucose [MG/dl] in Blood");
        observation.setCode(code);

        // Observation-Status setzen
        observation.setStatus(Observation.ObservationStatus.FINAL);

        // Observation auf dem FHIR-Server speichern
        IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseDstu3");
        MethodOutcome outcome = client.create().resource(observation).execute();


        //Überprüfen öb Übertragung erfolreich war
        if (outcome.getId() != null) {
            Log.d("FHIR", "Observation was successfully created with id: " + outcome.getId().getIdPart());
        } else {
            Log.e("FHIR", "Failed to create observation");
        }
    }


}