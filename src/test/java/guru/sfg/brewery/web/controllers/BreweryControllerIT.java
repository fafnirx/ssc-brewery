package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerIT extends BaseIT{

    private static final String BREWERY_BREWERIES_BASE_URL = "/brewery/breweries";

    @Test
    void listBreweriesWithoutAuth() throws Exception {
        mockMvc.perform(get(BREWERY_BREWERIES_BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listBreweriesWithUserRole() throws Exception {
        mockMvc.perform(get(BREWERY_BREWERIES_BASE_URL)
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }
    @Test
    void listBreweriesWithAdminRole() throws Exception {
        mockMvc.perform(get(BREWERY_BREWERIES_BASE_URL)
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesWithCustomerRole() throws Exception {
        mockMvc.perform(get(BREWERY_BREWERIES_BASE_URL)
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

}
