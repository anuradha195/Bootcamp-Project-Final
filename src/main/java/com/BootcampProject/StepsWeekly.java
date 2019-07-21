package com.BootcampProject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Controller;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

@Controller
public class StepsWeekly {
	/*
	 * TODO: -create new request for Google Fit -parse data received from Google
	 */
	// URL("https://www.googleapis.com/fitness/v1/users/me/dataSources") for Get
	// request parameter,
	// for post request:
	// https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate
	ArrayList<Date> dateListWeekly = new ArrayList<Date>();
	List<Integer> stepsListWeekly = new ArrayList<Integer>();

	public static void main(String[] args) throws Exception {
		StepsWeekly weeklySteps = new StepsWeekly();
		System.out.println("\nTesting 2 - Send Http POST request");
		weeklySteps.getJsonFromHttpRequest();
		weeklySteps.getWeeklyStepCount();

	}

	public String getJsonFromHttpRequest() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization",
				"Bearer ya29.GltMB4kDpSgASsFqo4B_Sp8TnGXJJBoLtJFYAzDztQVN2Rqn535tpPVWKgjZImlRvY_EVxyFhUM73OkyCSTAprx7Z_I0riRAptHm30zTeEyXSDP0ExUsOqPfYlst");
		// GET Today's date

		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		Calendar endcal = Calendar.getInstance();
		endcal.set(Calendar.HOUR, 0);
		endcal.set(Calendar.MINUTE, 0);
		endcal.set(Calendar.SECOND, 0);
		// System.out.println("Format Time Now: "+simpleformat.format(now.getTime()));
		endcal.set(Calendar.HOUR_OF_DAY, 0);
		long endtime = endcal.getTimeInMillis();
		Date startdate = new Date(endtime - (7 * DAY_IN_MS));
		long starttime = startdate.getTime();

		// Request parameters and other properties.
		httppost.setEntity(new StringEntity(" {\r\n" + "  \"aggregateBy\": [{\r\n" + "    \"dataSourceId\":\r\n"
				+ "      \"derived:com.google.step_count.delta:com.google.android.gms:estimated_steps\"\r\n"
				+ "  }],\r\n" + "  \"bucketByTime\": { \"durationMillis\": 86400000 },\r\n" + "  \"startTimeMillis\": "
				+ starttime + ",\r\n" + "  \"endTimeMillis\": " + endtime + "\r\n" + "}"));

		// Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			try (InputStream instream = entity.getContent()) {
				// do something useful
				StringWriter writer = new StringWriter();
				Scanner s = new Scanner(instream).useDelimiter("\\A");
				result = s.hasNext() ? s.next() : "";
				writer.close();

			}
			FileWriter fw = new FileWriter(
					"jsonWeekly.json");
			fw.write(result);
			System.out.println("Written json result to file");
			fw.close();
		}

		return result;
	}

	public void getWeeklyStepCount() throws Exception {

		InputStream file = new FileInputStream(
				"jsonWeekly.json");
		JsonReader reader = Json.createReader(file);
		JsonObject jsonObj = reader.readObject();
		reader.close();
		JsonArray jsonArrayBucket = jsonObj.getJsonArray("bucket");
		System.out.println("Json object is:"+jsonObj);
		for (Object projectObj : jsonArrayBucket.toArray()) {
			JsonObject project = (JsonObject) projectObj;
			JsonArray jsonArrayDataset = (JsonArray) project.get("dataset");

			// Get Date from json
			long timeInMillis = Long.parseLong(project.getString("startTimeMillis"));
			Date mydate = new Date(timeInMillis);
			java.sql.Date date = new java.sql.Date(mydate.getTime());

			for (Object issueObj : jsonArrayDataset.toArray()) {
				JsonObject datasetObj = (JsonObject) issueObj;
				JsonArray jsonArraypoint = (JsonArray) datasetObj.get("point");

				for (Object issueObj1 : jsonArraypoint.toArray()) {
					JsonObject pointObj = (JsonObject) issueObj1;
					JsonArray jsonArrayvalue = (JsonArray) pointObj.get("value");
					for (Object issueObj2 : jsonArrayvalue.toArray()) {
						JsonObject valueObj = (JsonObject) issueObj2;
						System.out.println(date + " --> Steps Count is: " + valueObj.getInt("intVal"));
						// Retrieved data and inserting into the database
						dateListWeekly.add(date);
						stepsListWeekly.add(valueObj.getInt("intVal"));
					}
				}
			}
		} // end of all for-loops

	} // end of Method : getWeeklyStepCount

}// end of class WeeklySteps
