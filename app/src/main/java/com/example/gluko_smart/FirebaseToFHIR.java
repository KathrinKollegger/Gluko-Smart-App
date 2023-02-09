package com.example.gluko_smart;


import android.util.Log;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FirebaseToFHIR {

    //Instanz FhirContext
   FhirContext ctx = FhirContext.forR4();



    //Methode zur Erstellung einer Observation

    public void createObservation(GlucoseValues glucoseValues)  {
        //Observation-Ressoruce erstellen

        Observation observation = new Observation();

        Log.i("Observation","created successfully");

        //ID der Observation setzen mit aktueller Uhrzeit wos hochladen
        observation.setId("Blutgluckose Selbstmessung" + System.currentTimeMillis());

        //Werte der Observation setzen (Blutzuckerwert)
        Quantity glucoseQuantity = new Quantity();
        glucoseQuantity.setValue(glucoseValues.getBzWert());
        glucoseQuantity.setUnit("mg/dL");

        observation.setValue(glucoseQuantity);
        Log.i("ObservationValue","set successfully");

        //Datum und Uhrzeit der Messung und
        // zuvor in ein DateTimeDt objekt konvertieren
        String dateString = glucoseValues.getTimestamp();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTimeType effectiveDateTime = new DateTimeType(date);

        observation.setEffective(effectiveDateTime);
        Log.i("ObservationDateTime","set successfully");

        // Patienten-Referenz setzen (Patient, zu dem die Observation gehört)
        Reference patientReference = new Reference();
        patientReference.setReference("Patient/7496229");
        observation.setSubject(patientReference);

        Log.i("ObservatioReferenz","created successfully");

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

        IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
        Log.i("GenericClient","created successfully");
        MethodOutcome outcome = client.create().resource(observation).execute();

        String outcomeStatus = outcome.getId().toString();

        //Überprüfen öb Übertragung erfolreich war
        if (outcome.getId() != null) {
            Log.d("FHIR", "Observation was successfully created with id: " + outcomeStatus);
        } else {
            Log.e("FHIR", "Failed to create observation");
        }
    }

}