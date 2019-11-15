package com.yourproject.resource.sample;

import com.yourproject.resource.model.mongo.Sample;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface SampleService {

    List<Sample> getSamplesByUsername(String username);

    List<GrantedAuthority> getUsernameAuthorities(String username);
}
