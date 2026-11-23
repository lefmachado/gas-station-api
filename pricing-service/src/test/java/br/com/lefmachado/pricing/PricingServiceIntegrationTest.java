package br.com.lefmachado.pricing;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
class PricingServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldExecuteFullFuelPriceCrudLifecycle() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/v1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "IT Pricing Gasoline",
                                  "literPrice": 5.899
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name", is("IT Pricing Gasoline")))
                .andExpect(jsonPath("$.literPrice", is(5.899)))
                .andExpect(jsonPath("$.updatedAt", notNullValue()))
                .andReturn();

        Number id = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/v1/prices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("IT Pricing Gasoline")));

        mockMvc.perform(put("/api/v1/prices/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "IT Pricing Gasoline Grid",
                                  "literPrice": 6.199
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("IT Pricing Gasoline Grid")))
                .andExpect(jsonPath("$.literPrice", is(6.199)));

        mockMvc.perform(delete("/api/v1/prices/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/prices/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void shouldReturnRfc7807ValidationErrorWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "literPrice": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.errors.name", notNullValue()))
                .andExpect(jsonPath("$.errors.literPrice", notNullValue()));
    }
}
