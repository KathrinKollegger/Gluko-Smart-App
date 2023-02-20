package com.example.gluko_smart;


import android.os.AsyncTask;
import android.util.Log;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.SimpleRequestHeaderInterceptor;

public class FirebaseToFHIR extends AsyncTask<GlucoseValues, Void, Boolean> {

    //http://hapi.fhir.org/search?serverId=home_r4&pretty=true&_summary=&resource=Observation&param.0.0=&param.0.1=7496229&param.0.name=patient&param.0.type=reference&sort_by=&sort_direction=&resource-search-limit=#

    //Instanz FhirContext
   FhirContext ctx = FhirContext.forR4();

    @Override
    protected Boolean doInBackground(GlucoseValues... glucoseValues) {
        GlucoseValues glucoseValue=glucoseValues[0];

        //Observation-Ressoruce erstellen
        Observation observation = new Observation();
        Log.i("Observation","created successfully");

        //create a Narrative object
        //Narrative --> menschenlesbare Darstellung der Ressource
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        XhtmlNode htmlNode= new XhtmlNode(NodeType.Element, "div");
        htmlNode.addText("<div xmlns=\"http://www.w3.org/1999/xhtml\"><p>Dies ist eine Blutzuckermessung mittels eines mobilen Blutzuckergeräts.</p></div>");
        narrative.setDiv(htmlNode);
        //set the Narrative on the Observation
        observation.setText(narrative);

        //ID der Observation setzen mit aktueller Uhrzeit wos hochladen
        observation.setId("Blutgluckose Selbstmessung" + System.currentTimeMillis());

        //Kategorie hinzufügen
        //Activity = Observations that measure or record any bodily activity that enhances or maintains physical fitness and overall health and wellness.
        //Not under direct supervision of practitioner such as a physical therapist. (e.g., laps swum, steps, sleep data)
        CodeableConcept category = new CodeableConcept();
        category.addCoding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("activity")
                .setDisplay("Activity");
        observation.setCategory(Collections.singletonList(category));

        //Werte der Observation setzen (Blutzuckerwert)
        Quantity glucoseQuantity = new Quantity();
        glucoseQuantity.setValue(glucoseValue.getBzWert());
        glucoseQuantity.setUnit("mg/dL");
        glucoseQuantity.setSystem("http://unitsofmeasure.org");
        observation.setValue(glucoseQuantity);
        Log.i("ObservationValue","set successfully");

        //Datum und Uhrzeit der Messung und
        // zuvor in ein DateTimeDt objekt konvertieren
        String dateString = glucoseValue.getTimestamp();
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date = simpleDateFormat.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTimeType effectiveDateTime = new DateTimeType(date);
        observation.setEffective(effectiveDateTime);
        Log.i("ObservationDateTime:","set successfully" + effectiveDateTime);

        // Patienten-Referenz setzen (Patient, zu dem die Observation gehört)
        Reference patientReference = new Reference();
        patientReference.setReference("Patient/7496229").setDisplay("Maxima Muster");
        observation.setSubject(patientReference);
        Log.i("ObservationReferenz","created successfully");

        //Code hinzufügen mit loinc Basis
        CodeableConcept code = new CodeableConcept();
        code.addCoding()
                .setSystem("http://loinc.org")
                .setCode("32016-8")
                .setDisplay("Glucose [mg/dl] in Capillary blood");
        observation.setCode(code);
        Log.i("CodeableConcept","created successfully");

        // Observation-Status setzen
        observation.setStatus(Observation.ObservationStatus.FINAL);
        Log.i("ObservationStatus","created successfully");

        //Practitioner hinzufügen
        Reference practitionerReference = new Reference();
        practitionerReference.setReference("Patient/7496229")
                .setDisplay("Maxima Muster");
        observation.addPerformer(practitionerReference);

        // Observation auf dem FHIR-Server speichern
        IGenericClient client = ctx.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
        Log.i("GenericClient","created successfully");

        //BenutzerAgenten für Anfordeurng setzen die an Server gesendet wird
        //HeaderValue: Http Header ; HeaderName: String der den Namen des Headers angibt
        client.registerInterceptor(new SimpleRequestHeaderInterceptor("User-Agent", "MyCustomUserAgent"));

        MethodOutcome outcome = client.create().resource(observation).execute();

        Log.i("FhirRessourceID","direct Link: "+outcome.getId().toString());
        return outcome.getId()!=null;

    }


        protected void onPostExecute(Boolean success){
        //Überprüfen ob Übertragung erfolreich war
            if (success) {
                Log.d("FHIR", "Observation was successfully created");

            } else {
                Log.e("FHIR", "Failed to create observation");
            }
    }


    }