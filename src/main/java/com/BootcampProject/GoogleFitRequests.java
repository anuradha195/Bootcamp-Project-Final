package com.BootcampProject;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;

class GoogleFitRequests {

    //private String dataType = "com.google.step_count.delta";
    private String accessToken = "";
    private long startTime = 0L;
    private long endTime = 0L;
    private Fitness fitness;

    GoogleFitRequests(User user) {
        this.accessToken = user.getAccessToken();
        setDates();
        authorizeUser();
    }

    private void authorizeUser() {
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        String appName = "BootcampProject";
        fitness = new Fitness.Builder(Utils.getDefaultTransport(), Utils.getDefaultJsonFactory(), null)
                .setHttpRequestInitializer(credential)
                .setApplicationName(appName)
                .build();
    }

    //create request for GoogleFit
    DailySteps[] requestData() throws IOException {
        BucketByTime bucketByTime = new BucketByTime();
        Long durationMillis = 86400000L;
        bucketByTime.setDurationMillis(durationMillis);
        AggregateRequest aggregateRequest = new AggregateRequest();
        String dataSourceId = "derived:com.google.step_count.delta:com.google.android.gms:estimated_steps";
        aggregateRequest.setAggregateBy(Collections.singletonList(new AggregateBy().setDataSourceId(dataSourceId)));
        aggregateRequest.setBucketByTime(bucketByTime);
        aggregateRequest.setStartTimeMillis(startTime);
        aggregateRequest.setEndTimeMillis(endTime);
        Fitness.Users.Dataset.Aggregate requestAgg = fitness.users().dataset().aggregate("me", aggregateRequest);
        AggregateResponse aggregateResponse = requestAgg.execute();
        return parseReceivedData(aggregateResponse);
    }

    //parse received data
    private DailySteps[] parseReceivedData(AggregateResponse aggregateResponse) {
        DailySteps[] dailySteps = new DailySteps[7];
        int steps = 0;
        int i = 0;
        for (AggregateBucket aggregateBucket : aggregateResponse.getBucket()) {
            steps = 0;
            for (Dataset dataset : aggregateBucket.getDataset()) {
                for (DataPoint dataPoint : dataset.getPoint()) {
                    for (Value value : dataPoint.getValue()) {
                        if (value.getIntVal() != null) {
                            steps += value.getIntVal();
                        }
                    }
                }
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(aggregateBucket.getStartTimeMillis());
            //int mYear = calendar.get(Calendar.YEAR);
            int calMonth = calendar.get(Calendar.MONTH) + 1;
            int calDay = calendar.get(Calendar.DAY_OF_MONTH);
            String year = Integer.toString(calendar.get(Calendar.YEAR));
            String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
            String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            if (calDay < 10) day = "0" + day;
            if (calMonth < 10) month = "0" + month;
            String date = day + "." + month + "." + year;//date formate
            DailySteps stepsInDay = new DailySteps();
            stepsInDay.setDate(date);
            stepsInDay.setSteps(steps);
            dailySteps[i] = stepsInDay;
            i++;
        }
        return dailySteps;
    }

    private void setDates() {
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        endDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), 3, 0);
        startDate.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH) - 7, 3, 0);
        this.startTime = startDate.getTimeInMillis();
        this.endTime = endDate.getTimeInMillis();
    }
}
