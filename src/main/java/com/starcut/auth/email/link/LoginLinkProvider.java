package com.starcut.auth.email.link;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.starcut.auth.email.configuration.EmailAuthConfiguration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class LoginLinkProvider implements LoginLinkProviderI {

	@Autowired
	private EmailAuthConfiguration config;

	@Value("${starcut.auth.email.lifetime-in-hours}")
	Integer jwtLifetimeInHours;

	@Value("${starcut.auth.email.endpoint}")
	String endPoint;

	@Value("${starcut.auth.email.redirect}")
	String redirection;

	public String produceJwt(String email, Duration validity, String location) throws UnsupportedEncodingException {
		return Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(email)
				.setIssuedAt(Date.from(Instant.now())).setExpiration(Date.from(Instant.now().plus(validity)))
				.claim("location", location).signWith(SignatureAlgorithm.HS256, config.getJwtSecret().getBytes("UTF-8"))
				.compact();
	}

	public String produceLoginLink(String email) throws UnsupportedEncodingException {
		return produceLoginLink(email, Duration.ofHours(jwtLifetimeInHours), redirection);
	}

	public String produceLoginLink(String email, Duration validity) throws UnsupportedEncodingException {
		return produceLoginLink(email, validity, redirection);
	}

	public String produceLoginLink(String email, String location) throws UnsupportedEncodingException {
		return produceLoginLink(email, Duration.ofHours(jwtLifetimeInHours), location);
	}

	@Override
	public String produceLoginLink(String email, Duration validity, String location)
			throws UnsupportedEncodingException {
		String jwt = this.produceJwt(email, validity, location);
		UriComponentsBuilder auth = ServletUriComponentsBuilder.fromCurrentServletMapping().path(endPoint);
		UriComponents link = auth.queryParam("token", jwt).build().encode();
		return link.toUriString();
	}

}
