@Test
void testRegisterWithExistingUser() {
    RegisterRequest registerRequest = new RegisterRequest("existingUser", "password", Role.MEMBER);
    User existingUser = new User("existingUser", "encodedPassword", Role.MEMBER);
    when(userRepository.findById("existingUser")).thenReturn(Optional.of(existingUser));

    assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
}

@Test
void testAuthenticateWithInvalidPassword() {
    AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "invalidPassword");
    User user = new User("username", "encodedPassword", Role.MEMBER);
    when(userRepository.findById("username")).thenReturn(Optional.of(user));

    assertThrows(BadCredentialsException.class, () -> authService.authenticate(authenticationRequest));
}

@Test
void testAuthenticateWithValidCredentials() {
    AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "password");
    User user = new User("username", "encodedPassword", Role.MEMBER);
    when(userRepository.findById("username")).thenReturn(Optional.of(user));
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new TestingAuthenticationToken(user, null));

    AuthenticationResponse response = authService.authenticate(authenticationRequest);

    assertNotNull(response.getAccessToken());
}
