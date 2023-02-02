package com.example.gluko_smart;

//habe ich in den gradle Files als implementation hinzugefügt
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

//da gebe ich das Layout der XML Datei vor
//value, unit und dateTime werden mithilfe von Annotationen von Simple XML-Serialization Libraryn serialisiert
@Root(name = "BloodGlucose")
public class BloodGlucose {

    @Element(name = "value")
    private float value;

    @Element(name = "unit")
    private String unit;

    @Element(name = "dateTime")
    private String dateTime;

    public BloodGlucose() {
    }

    public BloodGlucose(float value, String unit, String dateTime) {
        this.value = value;
        this.unit = unit;
        this.dateTime = dateTime;
    }

    //getter und setter methoden für diese Felder, die für die Daten verwendet werden können
    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}