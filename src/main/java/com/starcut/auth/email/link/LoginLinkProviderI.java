package com.starcut.auth.email.link;

import java.io.UnsupportedEncodingException;
import java.time.Duration;

public interface LoginLinkProviderI {
	
	public String produceLoginLink(String email, Duration validity, String location) throws UnsupportedEncodingException;
	
}
