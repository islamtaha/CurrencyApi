package com.formedix.exercise.repositories.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.formedix.exercise.exceptions.NotFoundException;
import com.formedix.exercise.models.DayRate;


@SpringBootTest
class RateRepositoryImplTest {

	@MockBean
	private ResourceLoader resourceLoader;
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private RateRepositoryImpl rateRepository;
	
	@BeforeEach
	public void before() throws IOException, ParseException {
		rateRepository = new RateRepositoryImpl(resourceLoader);
	}
	
	@Test
	void repositoryShouldReadAndSaveData() throws IOException, ParseException {
		InputStream cvsStream = new ByteArrayInputStream("Date,USD,CZK,CYP,\n2022-08-30,1.4,5,2,".getBytes());
		Resource resource = mock(Resource.class);
		when(resource.getInputStream()).thenReturn(cvsStream);
		when(resourceLoader.getResource("classpath:rates.csv")).thenReturn(resource);
		
		rateRepository.init();
		
		assertEquals(1.4, rateRepository.getRateInDate(df.parse("2022-08-30"), "USD"));			
	}
	
	@Test
	void repositoryShouldThrowNotFoundExceptionWhenCurrencyNotFound() throws ParseException, IOException {
		InputStream cvsStream = new ByteArrayInputStream("Date,USD,CZK,CYP,\n2022-08-30,1.4,N/A,2,".getBytes());
		Resource resource = mock(Resource.class);
		when(resource.getInputStream()).thenReturn(cvsStream);
		when(resourceLoader.getResource("classpath:rates.csv")).thenReturn(resource);
		
		rateRepository.init();
		
		Date date = df.parse("2022-08-30");

		final NotFoundException exception = assertThrows(
				NotFoundException.class,
                () -> rateRepository.getRateInDate(date, "CZK")
        );
        assertEquals("Currency CZK not found", exception.getMessage());	
	}
	
	@Test
	void repositoryShouldThrowNotFoundExceptionWhenDateNotFound() throws ParseException, IOException {
		InputStream cvsStream = new ByteArrayInputStream("Date,USD,CZK,CYP,\n2022-08-30,1.4,N/A,2,".getBytes());
		Resource resource = mock(Resource.class);
		when(resource.getInputStream()).thenReturn(cvsStream);
		when(resourceLoader.getResource("classpath:rates.csv")).thenReturn(resource);
		
		rateRepository.init();
		
		Date date = df.parse("2022-08-31");

		final NotFoundException exception = assertThrows(
				NotFoundException.class,
                () -> rateRepository.getRateInDate(date, "CZK")
        );
        assertEquals("Date 2022-08-31 not found", exception.getMessage());	
	}
	
	@Test
	void repositoryShouldReturnAllRatesInDate() throws ParseException, IOException {
		InputStream cvsStream = new ByteArrayInputStream("Date,USD,CZK,CYP,\n2022-08-30,1.4,N/A,2,\n2022-08-31,1.2,2.1,0".getBytes());
		Resource resource = mock(Resource.class);
		when(resource.getInputStream()).thenReturn(cvsStream);
		when(resourceLoader.getResource("classpath:rates.csv")).thenReturn(resource);
		
		rateRepository.init();
		
		Date date1 = df.parse("2022-08-30");
		Map<String, Double> actual1 = rateRepository.getAllCurrencyRatesInDate(date1);
		
		Map<String, Double> expected1 = new HashMap<>();
		expected1.put("USD", 1.4);
		expected1.put("CYP", 2.0);
		
		assertEquals(expected1, actual1);
		
		Date date2 = df.parse("2022-08-31");
		Map<String, Double> actual2 = rateRepository.getAllCurrencyRatesInDate(date2);
		
		Map<String, Double> expected2 = new HashMap<>();
		expected2.put("USD", 1.2);
		expected2.put("CZK", 2.1);
		expected2.put("CYP", 0.0);
		
		assertEquals(expected2, actual2);
	}
	
	@Test
	void repositoryShouldReturnAllRates() throws ParseException, IOException {
		InputStream cvsStream = new ByteArrayInputStream("Date,USD,CZK,CYP,\n2022-08-30,1.4,N/A,2,\n2022-08-31,1.2,2.1,0".getBytes());
		Resource resource = mock(Resource.class);
		when(resource.getInputStream()).thenReturn(cvsStream);
		when(resourceLoader.getResource("classpath:rates.csv")).thenReturn(resource);
		
		rateRepository.init();
		
		Map<Date, DayRate> actual = rateRepository.getAllRates();
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 1.4);
		day1.setCurrencyRate("CYP", 2.0);
		
		DayRate day2 = new DayRate();
		day2.setCurrencyRate("USD", 1.2);
		day2.setCurrencyRate("CZK", 2.1);
		day2.setCurrencyRate("CYP", 0.0);
		
		Map<Date, DayRate> expected = new HashMap<>();
		expected.put(df.parse("2022-08-30"), day1);
		expected.put(df.parse("2022-08-31"), day2);
		
		assertEquals(expected, actual);
	}
}
