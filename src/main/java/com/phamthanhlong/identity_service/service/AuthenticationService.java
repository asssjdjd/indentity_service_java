package com.phamthanhlong.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

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
import com.phamthanhlong.identity_service.dto.response.AuthenticationResponse;
import com.phamthanhlong.identity_service.dto.response.IntrospectResponse;
import com.phamthanhlong.identity_service.entity.User;
import com.phamthanhlong.identity_service.exception.ErorrCode;
import com.phamthanhlong.identity_service.exception.UserException;
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

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    // kiem tra token de xem nguoi dung co dung khong
    public IntrospectResponse introspect(IntrospeactRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY);

        //        parse token ve dang signedJWT
        SignedJWT signedJWT = SignedJWT.parse(token);

        var verified = signedJWT.verify(verifier);
        //      lay ra ngay het han
        Date expTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return IntrospectResponse.builder()
                .valid(verified && expTime.after(new Date()))
                .build();
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
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
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
