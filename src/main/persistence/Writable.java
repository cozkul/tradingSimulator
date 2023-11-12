package persistence;

import org.json.JSONObject;

// Represents the writable interface that returns object as an JSONObject
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
