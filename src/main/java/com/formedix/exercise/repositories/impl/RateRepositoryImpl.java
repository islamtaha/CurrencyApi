package com.formedix.exercise.repositories.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import com.formedix.exercise.models.AllRate;
import com.formedix.exercise.models.DayRate;
import com.formedix.exercise.repositories.RateRepository;
import com.opencsv.CSVReader;

@Repository
public class RateRepositoryImpl implements RateRepository {
	
    private Logger logger = LoggerFactory.getLogger(RateRepositoryImpl.class);
	
	private AllRate allRate;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	public RateRepositoryImpl() {}
	
	// for testing
	RateRepositoryImpl(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@PostConstruct
	public void init() throws IOException, ParseException {
		allRate = new AllRate();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		InputStream inputStream = resourceLoader.getResource("classpath:rates.csv").getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		List<List<String>> records = new ArrayList<>();
		try (CSVReader csvReader = new CSVReader(reader);) {
		    String[] values = null;
		    while ((values = csvReader.readNext()) != null) {
		        records.add(Arrays.asList(values));
		    }
		}
		List<String> currency = records.get(0);
		for(int i = 1; i < records.size(); i++) {
			Date date = df.parse(records.get(i).get(0));
			for(int j = 1; j < records.get(i).size(); j++) {
				if(!records.get(i).get(j).equals("N/A") && !records.get(i).get(j).isEmpty()) {
					String formatedDate = df.format(date);
					logger.debug("Date: {}, currency: {}, rate: {}", formatedDate, currency.get(j), records.get(i).get(j));
					allRate.setRateInDate(date, currency.get(j), Double.parseDouble(records.get(i).get(j)));
				}
			}
		}
	}
	
	public Double getRateInDate(Date date, String currency) {
		return allRate.getRateInDate(date, currency);
	}

	public Map<String, Double> getAllCurrencyRatesInDate(Date date) {
		return allRate.getAllCurrencyRatesInDate(date);
	}
	
	public Map<Date, DayRate> getAllRates() {
		return allRate.getAllRates();
	}
}
