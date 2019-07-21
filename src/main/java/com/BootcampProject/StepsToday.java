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

public class StepsToday {

	ArrayList<Date> dateListDaily = new ArrayList<Date>();
	List<Integer> stepsListDaily = new ArrayList<Integer>();
	
	public static void main(String[] args) throws Exception {
		StepsToday steps=new StepsToday();
		steps.getJsonFromHttpRequest();
		steps.getData();
	}

	public String getJsonFromHttpRequest() throws Exception {

		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate");
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("Authorization",
				"Bearer ya29.GltMB4qxkb_KxKuEUHpu_cLD1M5r-cjVhlDEGdL8EOYjaaqQS3sm4Yp9xqFU5iJpxqkaBGO-zfqU9q6MGIAbrzlwudKD_wo4bGk6iWh0Lk6Laa1SDSdfD5HUwRGW");
		
		// GET Today's date
		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		Calendar endcal = Calendar.getInstance();
		endcal.set(Calendar.HOUR, 0);
		endcal.set(Calendar.MINUTE, 0);
		endcal.set(Calendar.SECOND, 0);
		// System.out.println("Format Time Now: "+simpleformat.format(now.getTime()));
		endcal.set(Calendar.HOUR_OF_DAY, 0);
		long todayMidNightTime = endcal.getTimeInMillis();
		Date startdate = new Date(todayMidNightTime - (1 * DAY_IN_MS));
		long starttime = startdate.getTime();
		long endtime = System.currentTimeMillis();
		System.out.println("Start time : "+starttime);
		System.out.println("End time : "+endtime);

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

			}
			System.out.println(result);
			FileWriter fw = new FileWriter(
					"C:\\Users\\satpal kumar\\eclipse-workspace\\BootcampProject\\src\\main\\resources\\json\\jsonToday.json");
			fw.write(result);
			System.out.println("Written json result to file");
			fw.close();
		}

		return result;
	}

	public void getData() throws Exception {

			InputStream file = new FileInputStream(
					"C:\\Users\\satpal kumar\\eclipse-workspace\\BootcampProject\\src\\main\\resources\\json\\jsonToday.json");
			JsonReader reader = Json.createReader(file);
			JsonObject jsonObj = reader.readObject();
			reader.close();
			//Get json Array called bucket
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
							// Retrieved data and inserting into the List (dateList and Steps List)
							dateListDaily.add(date);
							stepsListDaily.add(valueObj.getInt("intVal"));
						}
					}
				}
			}
		}  //end of method getData()
	
} //end of class
