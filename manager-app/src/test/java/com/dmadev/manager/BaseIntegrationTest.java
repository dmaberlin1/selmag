package com.dmadev.manager;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WireMockTest(httpPort = 54321)
@RequiredArgsConstructor
public class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;



}
