package com.trackerapp.covidtracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.trackerapp.covidtracker.models.LocationStats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CoronaDataService {
    private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList();
    @PostConstruct
    @Scheduled(cron = "* * * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
                List<LocationStats> newStats = new ArrayList<>();
                HttpClient client = HttpClient.newHttpClient();
                 HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(DATA_URL))
                         .build();
                HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(httpResponse.body());
                StringReader csvBodyReader = new StringReader(httpResponse.body());
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
                for (CSVRecord record : records) {
                    LocationStats locationStat = new LocationStats();
                    locationStat.setState(record.get("Province/State"));
                    locationStat.setCountry(record.get("Region/Country"));
                    locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size() - 1)));
                }
    }
}
