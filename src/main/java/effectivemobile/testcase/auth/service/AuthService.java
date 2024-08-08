package effectivemobile.testcase.auth.service;

import effectivemobile.testcase.auth.model.JwtRequest;
import effectivemobile.testcase.auth.model.JwtResponse;
import effectivemobile.testcase.user.model.User;
import lombok.NonNull;

public interface AuthService {

    JwtResponse login(@NonNull JwtRequest jwtRequest);

    JwtResponse getAccessToken(@NonNull String refreshToken);

    JwtResponse refreshToken(@NonNull String refreshToken);

    User getAuthenticatedUser();
}
