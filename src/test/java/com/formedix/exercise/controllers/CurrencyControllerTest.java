package com.formedix.exercise.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.formedix.exercise.exceptions.BadRequestException;
import com.formedix.exercise.exceptions.NotFoundException;
import com.formedix.exercise.services.RateService;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyControllerTest {

	@Autowired
	private MockMvc mockMVC;
	
	@Autowired
	private RateService rateService;
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	@Test
	void getRatesReturnRatesWhenCorrectDate() throws Exception {
		Date date = df.parse("2022-08-01");
		Map<String, Double> expected = new HashMap<>();
		expected.put("USD", 1.001);
		when(rateService.getAllCurrencyRatesInDate(date)).thenReturn(expected);
		mockMVC
			.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates")
					.param("date", "2022-08-01")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).getAllCurrencyRatesInDate(date);
	}
	
	@Test
	void getRatesReturnBadRequestWhenMissingDate() throws Exception {
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "date parameter is missing");
		mockMVC
			.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void getRatesReturnBadRequestWhenInvalidDateFormat() throws Exception {
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "date does not match the expected type");
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates")
					.param("date", "2022-08")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void convertReturnResultWhenSourceTargetDateCorrect() throws Exception {
		String sourceCurrency = "USD";
		String targetCurrency = "CZK";
		Double amount = 1.0;
		String date = "2022-08-30";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("result", 20.0);
		
		when(rateService.convertAmount(sourceCurrency, targetCurrency, amount, df.parse(date))).thenReturn(20.0);
		
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/convert")
					.param("source", sourceCurrency)
					.param("target", targetCurrency)
					.param("amount", amount.toString())
					.param("date", date)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).convertAmount(sourceCurrency, targetCurrency, amount, df.parse(date));
	}
	
	@Test
	void convertReturnBadRequestWhenSourceLengthMoreThan3() throws Exception {
		String sourceCurrency = "USDE";
		String targetCurrency = "CZK";
		Double amount = 1.0;
		String date = "2022-08-30";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "convert.source: size must be between 3 and 3");
		
		when(rateService.convertAmount(sourceCurrency, targetCurrency, amount, df.parse(date))).thenReturn(20.0);
		
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/convert")
					.param("source", sourceCurrency)
					.param("target", targetCurrency)
					.param("amount", amount.toString())
					.param("date", date)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));		
	}
	
	@Test
	void convertReturnBadRequestWhenMissingAmount() throws Exception {
		String sourceCurrency = "USD";
		String targetCurrency = "CZK";
		String date = "2022-08-30";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "amount parameter is missing");
				
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/convert")
					.param("source", sourceCurrency)
					.param("target", targetCurrency)
					.param("date", date)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void maxReturnResultWhenStartFinishCurrencyCorrect() throws Exception {
		String start = "2022-08-10";
		String finish = "2022-08-30";
		String currency = "USD";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("result", 20.0);
		
		when(rateService.maxRateInRange(currency, df.parse(start), df.parse(finish))).thenReturn(20.0);
		
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/max")
					.param("start", start)
					.param("finish", finish)
					.param("currency", currency)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).maxRateInRange(currency, df.parse(start), df.parse(finish));
	}
	
	@Test
	void maxReturnBadRequestWhenCurrencyLengthLessThan3() throws Exception {
		String start = "2022-08-10";
		String finish = "2022-08-30";
		String currency = "US";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "max.currency: size must be between 3 and 3");
		
		when(rateService.maxRateInRange(currency, df.parse(start), df.parse(finish))).thenReturn(20.0);
		
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/max")
					.param("start", start)
					.param("finish", finish)
					.param("currency", currency)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void maxReturnBadRequestWhenMissingCurrency() throws Exception {
		String start = "2022-08-10";
		String finish = "2022-08-30";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "currency parameter is missing");
				
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/max")
					.param("start", start)
					.param("finish", finish)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void averageReturnResultWhenStartFinishCurrencyCorrect() throws Exception {
		String start = "2022-08-10";
		String finish = "2022-08-30";
		String currency = "USD";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("result", 20.0);
		
		when(rateService.averageRateInRange(currency, df.parse(start), df.parse(finish))).thenReturn(20.0);
		
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/average")
					.param("start", start)
					.param("finish", finish)
					.param("currency", currency)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).averageRateInRange(currency, df.parse(start), df.parse(finish));
	}
	
	@Test
	void averageReturnBadRequestWhenCurrencyLengthLessThan3() throws Exception {
		String start = "2022-08-10";
		String finish = "2022-08-30";
		String currency = "US";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "average.currency: size must be between 3 and 3");
		
		when(rateService.averageRateInRange(currency, df.parse(start), df.parse(finish))).thenReturn(20.0);
		
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/average")
					.param("start", start)
					.param("finish", finish)
					.param("currency", currency)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void averageReturnBadRequestWhenMissingStart() throws Exception {
		String finish = "2022-08-30";
		String currency = "USD";
		
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "start parameter is missing");
				
		mockMVC.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates/average")
					.param("finish", finish)
					.param("currency", currency)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
	}
	
	@Test
	void testNotFoundExceptionMessage() throws Exception {
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 404);
		expected.put("status", "NOT_FOUND");
		expected.put("message", "not found");
		
		Date date = df.parse("2022-08-01");
		when(rateService.getAllCurrencyRatesInDate(date)).thenThrow(new NotFoundException("not found"));
		mockMVC
			.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates")
					.param("date", "2022-08-01")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).getAllCurrencyRatesInDate(date);
	}
	
	@Test
	void testBadRequestExceptionMessage() throws Exception {
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 400);
		expected.put("status", "BAD_REQUEST");
		expected.put("message", "bad request");

		Date date = df.parse("2022-08-01");
		when(rateService.getAllCurrencyRatesInDate(date)).thenThrow(new BadRequestException("bad request"));
		mockMVC
			.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates")
					.param("date", "2022-08-01")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).getAllCurrencyRatesInDate(date);
	}
	
	@Test
	void testNullPointerExceptionMessage() throws Exception {
		Map<String, Object> expected = new HashMap<>();
		expected.put("code", 500);
		expected.put("status", "INTERNAL_SERVER_ERROR");
		expected.put("message", "Internal server error - Please reach out to the server administrator to resolve the issue");

		Date date = df.parse("2022-08-01");
		when(rateService.getAllCurrencyRatesInDate(date)).thenThrow(new NullPointerException());
		
		mockMVC
			.perform(
				MockMvcRequestBuilders
					.get("/api/v1/rates")
					.param("date", "2022-08-01")
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$").value(expected));
		
		verify(rateService).getAllCurrencyRatesInDate(date);
	}
}
