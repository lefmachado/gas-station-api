package br.com.lefmachado.gasstation;

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
class GasStationApiIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldExecuteFullRefuelingLifecyclePreserveHistoricalPriceAndEnforceReferentialIntegrity() throws Exception {
        // 1. Create Fuel Type
        MvcResult fuelTypeResult = mockMvc.perform(post("/api/v1/fuel-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "IT Gasoline",
                                  "literPrice": 5.899
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name", is("IT Gasoline")))
                .andExpect(jsonPath("$.literPrice", is(5.899)))
                .andReturn();

        Number fuelTypeId = JsonPath.read(fuelTypeResult.getResponse().getContentAsString(), "$.id");

        // 2. Create Fuel Pump linked to Fuel Type
        MvcResult pumpResult = mockMvc.perform(post("/api/v1/fuel-pumps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "IT Pump 1",
                                  "fuelTypeId": %d
                                }
                                """.formatted(fuelTypeId.longValue())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("IT Pump 1")))
                .andExpect(jsonPath("$.fuelType.id", is(fuelTypeId.intValue())))
                .andReturn();

        Number pumpId = JsonPath.read(pumpResult.getResponse().getContentAsString(), "$.id");

        // 3. Register Refueling informing only totalAmount (Scenario 1)
        MvcResult refuelingResult = mockMvc.perform(post("/api/v1/refuelings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fuelPumpId": %d,
                                  "totalAmount": 100.00
                                }
                                """.formatted(pumpId.longValue())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.liters", is(16.952)))
                .andExpect(jsonPath("$.totalAmount", is(100.00)))
                .andExpect(jsonPath("$.appliedLiterPrice", is(5.899)))
                .andReturn();

        Number refuelingId = JsonPath.read(refuelingResult.getResponse().getContentAsString(), "$.id");

        // 4. Update Fuel Type price and verify historical immutability (Scenario 3)
        mockMvc.perform(put("/api/v1/fuel-types/{id}", fuelTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "IT Gasoline",
                                  "literPrice": 6.299
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.literPrice", is(6.299)));

        mockMvc.perform(get("/api/v1/refuelings/{id}", refuelingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedLiterPrice", is(5.899)))
                .andExpect(jsonPath("$.totalAmount", is(100.00)));

        // 5. Referential integrity protection (Scenario 4)
        mockMvc.perform(delete("/api/v1/fuel-types/{id}", fuelTypeId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Conflict")))
                .andExpect(jsonPath("$.detail", notNullValue()));

        mockMvc.perform(delete("/api/v1/fuel-pumps/{id}", pumpId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Conflict")));
    }

    @Test
    void shouldReturnRfc7807ValidationErrorWhenFuelTypePayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/fuel-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   ",
                                  "literPrice": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.errors.name", notNullValue()))
                .andExpect(jsonPath("$.errors.literPrice", notNullValue()));
    }

    @Test
    void shouldReturnRfc7807NotFoundWhenFuelTypeDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/fuel-types/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Fuel type not found with id: 999999")));
    }

    @Test
    void shouldReturnRfc7807BadRequestWhenPathParameterTypeIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/fuel-types/invalid-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Invalid Parameter")))
                .andExpect(jsonPath("$.detail", is("Invalid value for parameter 'id'")));
    }
}
