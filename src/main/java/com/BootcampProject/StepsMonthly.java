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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

public class StepsMonthly {

	ArrayList<Date> dateListMonthly = new ArrayList<Date>();
	List<Integer> stepsListMonthly = new ArrayList<Integer>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		StepsMonthly req = new StepsMonthly();
		req.getJsonFromHttpRequest();
		req.getMonthlyStepCount();

	}

	public String getJsonFromHttpRequest() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization",
				"Bearer ya29.GltMB4qxkb_KxKuEUHpu_cLD1M5r-cjVhlDEGdL8EOYjaaqQS3sm4Yp9xqFU5iJpxqkaBGO-zfqU9q6MGIAbrzlwudKD_wo4bGk6iWh0Lk6Laa1SDSdfD5HUwRGW");
		httppost.setEntity(new StringEntity(" {\r\n" + "  \"aggregateBy\": [{\r\n" + "    \"dataSourceId\":\r\n"
				+ "      \"derived:com.google.step_count.delta:com.google.android.gms:estimated_steps\"\r\n"
				+ "  }],\r\n" + "  \"bucketByTime\": { \"durationMillis\": 86400000 },\r\n" + "  \"startTimeMillis\": "
				+ "1559336400000" + ",\r\n" + "  \"endTimeMillis\": " + "1561928400000" + "\r\n" + "}"));

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

			}
			FileWriter fw = new FileWriter(
					"C:\\Users\\satpal kumar\\Downloads\\BootcampProject-master\\src\\main\\resources\\json\\jsonMonthly.json");
			fw.write(result);
			System.out.println("Written json result to file");
			fw.close();
		}

		return result;
	} // end of method: getJsonFromHttpRequest

	public void getMonthlyStepCount() throws Exception {

		InputStream file = new FileInputStream(
				"C:\\Users\\satpal kumar\\Downloads\\BootcampProject-master\\src\\main\\resources\\json\\jsonMonthly.json");
		JsonReader reader = Json.createReader(file);
		JsonObject jsonObj = reader.readObject();
		reader.close();
		JsonArray jsonArrayBucket = jsonObj.getJsonArray("bucket");

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
						dateListMonthly.add(date);
						int x = valueObj.getInt("intVal");
						stepsListMonthly.add(x);

					}
				}
			}
		}
	} // end of method: getMonthlyStepCount

} // end of class
