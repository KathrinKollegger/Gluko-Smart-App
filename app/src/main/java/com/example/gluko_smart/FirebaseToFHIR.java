package com.example.gluko_smart;

import static com.example.gluko_smart.GlobalVariable.*;

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

/**
 * AsyncTask responsible for converting GlucoseValues from Firebase Realtime DB
 * into FHIR Observation Resources and saves it on a FHIR Server.
 * The Observation contains the glucose value, date and time of the measurement,
 * patient reference, code, and practitioner reference.
 * <p>
 * Link to generated FHIR Observation Resources for Test Patient Maxima Muster (Resource ID: 7496229)
 * http://hapi.fhir.org/search?serverId=home_r4&pretty=true&_summary=&resource=Observation&param.0.0=&param.0.1=7496229&param.0.name=patient&param.0.type=reference&sort_by=&sort_direction=&resource-search-limit=#
 */

public class FirebaseToFHIR extends AsyncTask<GlucoseValues, Void, Boolean> {

    //Instanz of FHIRContext for
    FhirContext ctx = FhirContext.forR4();


    @Override
    protected Boolean doInBackground(GlucoseValues... glucoseValues) {
        GlucoseValues glucoseValue = glucoseValues[0];

        //create Observation resource
        Observation observation = new Observation();
        Log.i(TAG_FHIR, "Observation created successfully");

        //create a Narrative object
        //Narrative --> humanreadable Sequence of Resource
        Narrative narrative = new Narrative();
        narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
        XhtmlNode htmlNode = new XhtmlNode(NodeType.Element, "div");
        htmlNode.addText("<div xmlns=\"http://www.w3.org/1999/xhtml\"><p>Dies ist eine Blutzuckermessung mittels eines mobilen Blutzuckergeräts.</p></div>");
        narrative.setDiv(htmlNode);

        //set the Narrative on the Observation
        observation.setText(narrative);

        //Set ID of Observation, even though unique ID is provide when Resource is created
        observation.setId("Blutgluckose Selbstmessung" + System.currentTimeMillis());

        //Add Category
        //Activity = Observations that measure or record any bodily activity that enhances or maintains physical fitness and overall health and wellness.
        //Not under direct supervision of practitioner such as a physical therapist. (e.g., laps swum, steps, sleep data)
        CodeableConcept category = new CodeableConcept();
        category.addCoding().setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("activity")
                .setDisplay("Activity");
        observation.setCategory(Collections.singletonList(category));

        //Set value of Observation (Blood Glucose)
        Quantity glucoseQuantity = new Quantity();
        glucoseQuantity.setValue(glucoseValue.getBzWert());

        //set Unit of Observation - in App always mg/dl
        glucoseQuantity.setUnit("mg/dL");
        glucoseQuantity.setSystem("http://unitsofmeasure.org");
        observation.setValue(glucoseQuantity);
        Log.i(TAG_FHIR, "Value set successfully");

        //Set effectiveDateTime of Observation
        String dateString = glucoseValue.getTimestamp();
        Date date = null;

        //Parse Timestamp into Date -> DateTimeType
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date = simpleDateFormat.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTimeType effectiveDateTime = new DateTimeType(date);
        observation.setEffective(effectiveDateTime);
        Log.i(TAG_FHIR, "EffectiveDateTime set successfully" + effectiveDateTime);

        //Set Patient Reference (Testpatient Maxima Muster)
        //Adapt here to reference different FHIR Patient-Resources
        Reference patientReference = new Reference();
        patientReference.setReference("Patient/7496229").setDisplay("Maxima Muster");
        observation.setSubject(patientReference);
        Log.i(TAG_FHIR, "PatientReferenz created successfully");

        //Add Code for Observation - in Code System LOINC
        CodeableConcept code = new CodeableConcept();
        code.addCoding()
                .setSystem("http://loinc.org")
                .setCode("32016-8")
                .setDisplay("Glucose [mg/dl] in Capillary blood");
        observation.setCode(code);
        Log.i(TAG_FHIR, "CodeableConcept created successfully");

        //Set Status for Observation
        observation.setStatus(Observation.ObservationStatus.FINAL);
        Log.i(TAG_FHIR, "ObservationStatus created successfully");

        //add Practitioner
        Reference practitionerReference = new Reference();
        practitionerReference.setReference("Patient/7496229")
                .setDisplay("Maxima Muster");
        observation.addPerformer(practitionerReference);

        //Create RESTfulGeneric Client for used FHIR Server
        IGenericClient client = ctx.newRestfulGenericClient("https://hapi.fhir.org/baseR4");
        Log.i("GenericClient", "created successfully");

        //create Custom User Agent Header
        client.registerInterceptor(new SimpleRequestHeaderInterceptor("User-Agent", "MyCustomUserAgent"));

        MethodOutcome outcome = client.create().resource(observation).execute();

        //Post Link to created FHIR Ressource into LOG
        Log.i("FhirRessourceID", "direct Link: " + outcome.getId().toString());

        return outcome.getId() != null;
    }

    //checking if Creation and Transfer of Resource were successful
    protected void onPostExecute(Boolean success) {
        if (success) {
            Log.d("FHIR", "Observation was successfully created");

        } else {
            Log.e("FHIR", "Failed to create observation");
        }
    }

}