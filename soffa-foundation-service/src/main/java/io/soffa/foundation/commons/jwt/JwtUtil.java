package io.soffa.foundation.commons.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.soffa.foundation.commons.DateUtil;
import io.soffa.foundation.commons.IOUtil;
import io.soffa.foundation.exceptions.TechnicalException;
import io.soffa.foundation.logging.Logger;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public final class JwtUtil {

    private static final Logger LOG = Logger.create(JwtUtil.class);

    private JwtUtil() {
    }

    //

    @SneakyThrows
    public static String create(final String issuer, final String secretKey,
                                final String subject, final Map<String, Object> claims,
                                final int timeToLiveInMinutes) {
        return create(issuer, secretKey, subject, claims, timeToLiveInMinutes,EncryptionMethod.A128GCM);
    }
    @SneakyThrows
    public static String create(final String issuer, final String secretKey,
                                final String subject, final Map<String, Object> claims,
                                final int timeToLiveInMinutes, final EncryptionMethod encryptionMethod) {
        final Date issuedAt = new Date();
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .issueTime(issuedAt)
                .expirationTime(DateUtil.plusMinutes(issuedAt, timeToLiveInMinutes));
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            claimsSetBuilder.claim(entry.getKey(), entry.getValue());
        }
        JWTClaimsSet claimsSet = claimsSetBuilder.build();

        try {
            Payload payload = new Payload(claimsSet.toJSONObject());
            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, encryptionMethod);
            DirectEncrypter encrypter = new DirectEncrypter(secretKey.getBytes());
            JWEObject jweObject = new JWEObject(header, payload);
            jweObject.encrypt(encrypter);
            return jweObject.serialize();
        } catch (JOSEException e) {
            throw new TechnicalException("Unable to create JWT", e);
        }
    }


    @SneakyThrows
    public static String fromJwks(final InputStream jwkSource, final String issuer, final String subject, final Map<String, Serializable> claims) {
        String jwkString = IOUtil.toString(jwkSource).orElseThrow(() -> new TechnicalException("INVALID_JWK_SOURCE"));
        if (LOG.isTraceEnabled()) {
            LOG.trace("Using JWK: %s", jwkString);
        }
        JSONObject json = new JSONObject(jwkString);
        if (json.has("keys")) {
            json = json.getJSONArray("keys").getJSONObject(0);
        }
        JWK jwk = JWK.parse(new net.minidev.json.JSONObject(json.toMap()));
        RSAKey rsaJWK = jwk.toRSAKey();
        JWSSigner signer = new RSASSASigner(rsaJWK);
        Date issuedAt = new Date();
        JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder().subject(subject).issuer(issuer)
                .issueTime(issuedAt)
                .expirationTime(DateUtil.plusHours(issuedAt, 1));
        if (claims != null) {
            for (Map.Entry<String, Serializable> entry : claims.entrySet()) {
                claimsSetBuilder.claim(entry.getKey(), entry.getValue());
            }
        }
        JWTClaimsSet claimsSet = claimsSetBuilder.build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

}
