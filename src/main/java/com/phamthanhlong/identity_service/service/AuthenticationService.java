package com.phamthanhlong.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phamthanhlong.identity_service.dto.request.AuthenticationRequest;
import com.phamthanhlong.identity_service.dto.request.IntrospeactRequest;
import com.phamthanhlong.identity_service.dto.request.LogoutRequest;
import com.phamthanhlong.identity_service.dto.request.RefreshTokenRequest;
import com.phamthanhlong.identity_service.dto.response.AuthenticationResponse;
import com.phamthanhlong.identity_service.dto.response.IntrospectResponse;
import com.phamthanhlong.identity_service.entity.InvalidatedToken;
import com.phamthanhlong.identity_service.entity.User;
import com.phamthanhlong.identity_service.exception.ErorrCode;
import com.phamthanhlong.identity_service.exception.UserException;
import com.phamthanhlong.identity_service.repository.InvalidatedTokenRepository;
import com.phamthanhlong.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(),true);

        // logut token
        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        // tao token moi tu user
        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(ErorrCode.UNAUTHORIZED));

        var token = getCreateToken(user);
        //        tao token
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        try{
            SignedJWT signedJWT = verifyToken(token,true);

            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        }catch (UserException e) {
            log.info("token already expiry");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY);

        //        parse token ve dang signedJWT
        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);
        //      lay ra ngay het han
        Date expTime = (isRefresh)
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!(verified && expTime.after(new Date()))) throw new UserException(ErorrCode.UNAUTHORIZED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new UserException(ErorrCode.UNAUTHORIZED);
        }

        return signedJWT;
    }

    // kiem tra token de xem nguoi dung co dung khong
    public IntrospectResponse introspect(IntrospeactRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isvalidate = true;
        try {
            verifyToken(token,false);
        } catch (UserException e) {
            isvalidate = false;
        }
        return IntrospectResponse.builder().valid(isvalidate).build();
    }

    // tao token neu nguoi dung dang nhap dung
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        //        tim nguoi dung
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new UserException(ErorrCode.USERNOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        //      xac thuc thuat toan
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new UserException(ErorrCode.UNAUTHENTICATED);

        var token = getCreateToken(user);
        //        tao token
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    //    tao token tu username
    private String getCreateToken(User user) {
        //        tao header
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        //        tao cac claim

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("phamthanhlong")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        //        tao payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        //        tao object
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("cannot create token", e);
            throw new RuntimeException(e);
        }
    }
    //    roles
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }
        return stringJoiner.toString();
    }
}
