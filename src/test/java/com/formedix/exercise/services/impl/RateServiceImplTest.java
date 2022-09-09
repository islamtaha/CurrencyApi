package com.formedix.exercise.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.formedix.exercise.exceptions.BadRequestException;
import com.formedix.exercise.exceptions.NotFoundException;
import com.formedix.exercise.models.DayRate;
import com.formedix.exercise.repositories.RateRepository;
import com.formedix.exercise.services.RateService;

@SpringBootTest
class RateServiceImplTest {
	
	private RateService rateService;
	
	@Autowired
	private RateRepository rateRepository;
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	@BeforeEach
	public void before() {
		rateService = new RateServiceImpl(rateRepository);
	}

	@Test
	void getAllCurrencyRatesInDateShouldCallRateRepository() throws ParseException {
		Date date = df.parse("2022-08-30");
		
		Map<String, Double> expected = new HashMap<>();
		expected.put("USD", 1.001);
		
		when(rateRepository.getAllCurrencyRatesInDate(date)).thenReturn(expected);
		
		Map<String, Double> actual = rateService.getAllCurrencyRatesInDate(date);
		
		assertEquals(expected, actual);
		verify(rateRepository).getAllCurrencyRatesInDate(date);
	}
	
	@Test
	void convertAmountReturnCorrectResult() throws ParseException {
		String sourceCurrency = "USD";
		String targetCurrency = "CZK";
		Double amount = 2.0;
		Date date = df.parse("2022-08-30");
		
		when(rateRepository.getRateInDate(date, sourceCurrency)).thenReturn(2.0);
		when(rateRepository.getRateInDate(date, targetCurrency)).thenReturn(24.0);

		Double actual = rateService.convertAmount(sourceCurrency, targetCurrency, amount, date);
		Double expected = 24.0;
		
		assertEquals(expected, actual);
		
		verify(rateRepository).getRateInDate(date, sourceCurrency);
		verify(rateRepository).getRateInDate(date, targetCurrency);
	}
	
	@Test
	void maxRateInRangeReturnCorrectResultWhenCorrectRange() throws ParseException {
		
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 2.0);
		day1.setCurrencyRate("XYZ", 3.0);
		
		DayRate day2 = new DayRate();
		day2.setCurrencyRate("USD", 1.5);
		
		Map<Date, DayRate> rates = new HashMap<>();
		rates.put(df.parse("2022-08-30"), day1);
		rates.put(df.parse("2022-08-20"), day2);

		
		when(rateRepository.getAllRates()).thenReturn(rates);

		assertEquals(2.0, rateService.maxRateInRange("USD", df.parse("2022-08-20"), df.parse("2022-08-30")));
		assertEquals(2.0, rateService.maxRateInRange("USD", df.parse("2022-08-10"), df.parse("2022-08-35")));
		assertEquals(1.5, rateService.maxRateInRange("USD", df.parse("2022-08-10"), df.parse("2022-08-21")));
		assertEquals(1.5, rateService.maxRateInRange("USD", df.parse("2022-08-10"), df.parse("2022-08-20")));
		assertEquals(3.0, rateService.maxRateInRange("XYZ", df.parse("2022-08-30"), df.parse("2022-08-30")));
	}
	
	@Test
	void maxRateInRangeThrowNotFoundExceptionWhenInvalidRangeOrCurrency() throws ParseException {
		Date start1 = df.parse("2022-08-20");
		Date finish1 = df.parse("2022-08-20");
		
		Date start2 = df.parse("2022-08-30");
		Date finish2 = df.parse("2022-08-30");
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 2.0);
		
		Map<Date, DayRate> rates = new HashMap<>();
		rates.put(df.parse("2022-08-30"), day1);

		
		when(rateRepository.getAllRates()).thenReturn(rates);
		
		final NotFoundException exception1 = assertThrows(
				NotFoundException.class,
                () -> rateService.maxRateInRange("USD", start1, finish1)
        );
        assertEquals("Result not found for input[currency: USD, start: 2022-08-20, finsih: 2022-08-20]", exception1.getMessage());
        
        final NotFoundException exception2 = assertThrows(
				NotFoundException.class,
                () -> rateService.maxRateInRange("USDX", start2, finish2)
        );
        assertEquals("Result not found for input[currency: USDX, start: 2022-08-30, finsih: 2022-08-30]", exception2.getMessage());
	}
	
	@Test
	void maxRateInRangeThrowBadRequestExceptionWhenFinishBeforeStart() throws ParseException {
		Date start = df.parse("2022-08-20");
		Date finish = df.parse("2022-08-10");
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 2.0);
		
		Map<Date, DayRate> rates = new HashMap<>();
		rates.put(df.parse("2022-08-30"), day1);

		
		when(rateRepository.getAllRates()).thenReturn(rates);
		
		final BadRequestException exception = assertThrows(
				BadRequestException.class,
                () -> rateService.maxRateInRange("USD", start, finish)
        );
        assertEquals("date range [2022-08-20 - 2022-08-10] is incorrect - start must be before finish", exception.getMessage());
	}
	
	@Test
	void averageRateInRangeReturnCorrectResultWhenCorrectRange() throws ParseException {
		
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 2.0);
		day1.setCurrencyRate("XYZ", 1.5);
		
		DayRate day2 = new DayRate();
		day2.setCurrencyRate("USD", 4.0);
		
		Map<Date, DayRate> rates = new HashMap<>();
		rates.put(df.parse("2022-08-30"), day1);
		rates.put(df.parse("2022-08-20"), day2);

		
		when(rateRepository.getAllRates()).thenReturn(rates);

		assertEquals(3.0, rateService.averageRateInRange("USD", df.parse("2022-08-20"), df.parse("2022-08-30")));
		assertEquals(3.0, rateService.averageRateInRange("USD", df.parse("2022-08-10"), df.parse("2022-08-35")));
		assertEquals(4.0, rateService.averageRateInRange("USD", df.parse("2022-08-10"), df.parse("2022-08-21")));
		assertEquals(4.0, rateService.averageRateInRange("USD", df.parse("2022-08-10"), df.parse("2022-08-20")));
		assertEquals(1.5, rateService.averageRateInRange("XYZ", df.parse("2022-08-30"), df.parse("2022-08-30")));
	}
	
	@Test
	void averageRateInRangeThrowNotFoundExceptionWhenInvalidRangeOrCurrency() throws ParseException {
		Date start1 = df.parse("2022-08-20");
		Date finish1 = df.parse("2022-08-20");
		
		Date start2 = df.parse("2022-08-30");
		Date finish2 = df.parse("2022-08-30");
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 2.0);
		
		Map<Date, DayRate> rates = new HashMap<>();
		rates.put(df.parse("2022-08-30"), day1);

		
		when(rateRepository.getAllRates()).thenReturn(rates);
		
		final NotFoundException exception1 = assertThrows(
				NotFoundException.class,
                () -> rateService.averageRateInRange("USD", start1, finish1)
        );
        assertEquals("Result not found for input[currency: USD, start: 2022-08-20, finsih: 2022-08-20]", exception1.getMessage());
        
        final NotFoundException exception2 = assertThrows(
				NotFoundException.class,
                () -> rateService.averageRateInRange("USDX", start2, finish2)
        );
        assertEquals("Result not found for input[currency: USDX, start: 2022-08-30, finsih: 2022-08-30]", exception2.getMessage());
	}
	
	@Test
	void averageRateInRangeThrowBadRequestExceptionWhenFinishBeforeStart() throws ParseException {
		Date start = df.parse("2022-08-20");
		Date finish = df.parse("2022-08-10");
		
		DayRate day1 = new DayRate();
		day1.setCurrencyRate("USD", 2.0);
		
		Map<Date, DayRate> rates = new HashMap<>();
		rates.put(df.parse("2022-08-30"), day1);

		
		when(rateRepository.getAllRates()).thenReturn(rates);
		
		final BadRequestException exception = assertThrows(
				BadRequestException.class,
                () -> rateService.averageRateInRange("USD", start, finish)
        );
        assertEquals("date range [2022-08-20 - 2022-08-10] is incorrect - start must be before finish", exception.getMessage());
	}
}
