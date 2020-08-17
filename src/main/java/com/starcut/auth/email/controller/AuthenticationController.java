package com.starcut.auth.email.controller;

import java.io.UnsupportedEncodingException;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.starcut.auth.common.CustomUserToken;
import com.starcut.auth.common.RoleProviderI;
import com.starcut.auth.common.db.EmailUserRepositoryI;
import com.starcut.auth.common.db.UserI;
import com.starcut.auth.common.jwt.JwtAuthenticatedUser;
import com.starcut.auth.email.configuration.EmailAuthConfiguration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Controller
@RequestMapping("/auth")
public class AuthenticationController<U extends UserI> {

	private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	@Autowired
	private EmailUserRepositoryI<U> userRepository;

	@Autowired
	private RoleProviderI<U> roleProvider;

	@Autowired
	private EmailAuthConfiguration config;

	@RequestMapping(method = RequestMethod.GET)
	public String auth(@RequestParam(name = "token") String jwt) {
		try {
			Jws<Claims> token = Jwts.parser().setSigningKey(config.getJwtSecret().getBytes("UTF-8"))
					.parseClaimsJws(jwt);
			String subject = token.getBody().getSubject();
			LOGGER.info("Received token for principal: " + subject);
			U user = userRepository.findByEmail(subject).orElse(null);
			LOGGER.info(roleProvider.getGrantedAuthoritiesFor(user).toString());
			JwtAuthenticatedUser<U> authenticatedUser = new JwtAuthenticatedUser<>(subject, null, jwt);
			String redirection = "redirect:/";
			String location = (String) token.getBody().get("location");
			if (location != null) {
				redirection = "redirect:" + token.getBody().get("location");
			}

			SecurityContextHolder.getContext().setAuthentication(new CustomUserToken<JwtAuthenticatedUser<U>>(
					authenticatedUser, roleProvider.getGrantedAuthoritiesFor(user)));

			LOGGER.info("User " + subject + " has been authenticated. Redirecting to " + redirection);
			return redirection;

		} catch (SignatureException e) {
			LOGGER.error("Invalid signature for the token: " + jwt);
		} catch (ExpiredJwtException e) {
			LOGGER.error("Expired token: " + jwt);
		} catch (EntityNotFoundException e) {
			LOGGER.error("Cannot find the user with the principal present in the token: " + jwt);
		} catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException
				| UnsupportedEncodingException e) {
			LOGGER.error("Unexpected error during processing the JWT token: " + jwt, e);
		}

		return "redirect:/token";
	}
}
