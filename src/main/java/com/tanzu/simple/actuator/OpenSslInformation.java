package com.tanzu.simple.actuator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import com.tanzu.simple.utils.ProcessLauncher;

// https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-application-info-custom
@Component
public class OpenSslInformation implements InfoContributor {

  private static final Logger LOG = LoggerFactory.getLogger(OpenSslInformation.class);

  @Override
  public void contribute(Info.Builder builder) {

    StringBuilder result = new StringBuilder();
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

      ProcessLauncher processLauncher = new ProcessLauncher(bos, 5000);
      processLauncher.exec("openssl version -a");

      String output = bos.toString(StandardCharsets.UTF_8);
      if (null != output) {
        String[] parts = output.split("\n", 4);
        if (parts.length > 2) {
          result.append(parts[0]).append(" - ").append(parts[1]);
        } else {
          LOG.warn("Bad result from openssl command {}", output);
        }
      }

    } catch (IOException e) {
      LOG.warn("An error occurred while retrieving Openssl version - {}", e.getMessage());
    }

    builder.withDetail("openssl", result.toString());
  }
}
