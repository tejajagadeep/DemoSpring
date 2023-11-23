public class AuthService {
    @Autowired
    AuthenticationClient authenticationClient;

    public Map<String, String> validateToken(String token) {
        HttpEntity<String> entity = getHttpEntityWithHeaders(token);
        try {
            ResponseEntity<Map<String, String>> response = authenticationClient.validateToken(token);
//                    restTemplate.exchange
//                            (baseUrl + "/validate", HttpMethod.POST,
//                                    entity, new ParameterizedTypeReference<Map<String, String>>() {
//            });

            System.out.println(response.getBody()+"from*********");
            System.out.println(response.getBody()+"from*********");
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to validate token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("connection refused: " + e.getMessage());
        }
    }

    private HttpEntity<String> getHttpEntityWithHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        return new HttpEntity<>("", headers);
    }
}
