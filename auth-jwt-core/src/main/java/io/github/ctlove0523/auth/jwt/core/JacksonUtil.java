package io.github.ctlove0523.auth.jwt.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JacksonUtil {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private JacksonUtil() {
		throw new UnsupportedOperationException("JacksonUtil");
	}

	public static String pojo2Json(Object pojo) {
		Objects.requireNonNull(pojo, "pojo");

		try {
			return MAPPER.writeValueAsString(pojo);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static <T> T json2Pojo(String jsonString, Class<T> clazz) {
		Objects.requireNonNull(jsonString, "jsonString");
		Objects.requireNonNull(clazz, "clazz");

		try {
			return MAPPER.readValue(jsonString, clazz);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Map<String, String> json2Map(String jsonString) {
		Objects.requireNonNull(jsonString, "jsonString");

		try {
			return MAPPER.readValue(jsonString, new TypeReference<Map<String, String>>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return new HashMap<>();
	}
}
