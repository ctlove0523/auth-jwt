package io.github.ctlove0523.auth.jwt.core;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static String pojo2Json(Object o) {
		Objects.requireNonNull(o, "o");
		try {
			return MAPPER.writeValueAsString(o);
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
}
