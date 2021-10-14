package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {
    static final String BEER_API_BASE_URL="/api/v1/beer/";
    static final String BEER_UPC_API_BASE_URL="/api/v1/beerUpc/";

    @Autowired
    BeerRepository beerRepository;
    @Autowired
    BeerOrderRepository beerOrderRepository;

    @DisplayName("Delete tests")
    @Nested
    class DeleteTests {
        public Beer beerToDelete() {
            Random rand = new Random();
            return beerRepository.saveAndFlush(Beer.builder()
                    .beerName("Delete Me Beer")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(String.valueOf(rand.nextInt(99999999)))
                    .build()
            );
        }

        @Test
        void deleteBeerUrlWithAdminRole() throws Exception {
            mockMvc.perform(delete(BEER_API_BASE_URL+ beerToDelete().getId())
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        void deleteBeerUrlWithUserRole() throws Exception {
            mockMvc.perform(delete(BEER_API_BASE_URL+ beerToDelete().getId())
                            .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerUrlWithCustomerRole() throws Exception {
            mockMvc.perform(delete(BEER_API_BASE_URL+ beerToDelete().getId())
                            .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteBeerUrlNoAuth() throws Exception {
            mockMvc.perform(delete(BEER_API_BASE_URL+ beerToDelete().getId()))
                    .andExpect(status().isUnauthorized());
        }

    }

    @Test
    void findBeers() throws Exception {
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        mockMvc.perform(get(BEER_API_BASE_URL+ beer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUpc() throws Exception {
        mockMvc.perform(get(BEER_UPC_API_BASE_URL + DefaultBreweryLoader.BEER_1_UPC))
                .andExpect(status().isOk());
    }
}
