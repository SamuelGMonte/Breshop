package br.com.breshop.util;


import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenExpiration {

    public static boolean isTokenExpired(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);

            long exp = jsonNode.get("exp").asLong();  // Get expiration time from token (exp in seconds)
            long currentTimeInSeconds = System.currentTimeMillis() / 1000;  // Current time in seconds

            return exp < currentTimeInSeconds;  // Return true if the token is expired
        } catch (Exception e) {
            throw new RuntimeException("Error while decoding token", e);
        }
    }
}
