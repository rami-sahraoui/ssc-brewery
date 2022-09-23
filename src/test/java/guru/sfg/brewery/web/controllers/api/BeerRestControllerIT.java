package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
class BeerRestControllerIT extends BaseIT {
    
    @Autowired
    BeerRepository beerRepository;
    
    @Autowired
    BeerOrderRepository beerOrderRepository;

    Beer beerToManipulate() {

        Random rand = new Random();

        return beerRepository.saveAndFlush(
                Beer.builder()
                        .beerName("Manipulate Me Beer")
                        .beerStyle(BeerStyleEnum.IPA)
                        .minOnHand(12)
                        .quantityToBrew(200)
                        .upc(String.valueOf(rand.nextInt(999999999)))
                        .build()
        );
    }

    @Test
    void findBeers() throws Exception{
        mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
    void findBeersAUTH(String user, String pwd) throws Exception{
        mockMvc.perform(get("/api/v1/beer")
                        .with(httpBasic(user, pwd)))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerById() throws Exception{
        mockMvc.perform(get("/api/v1/beer/" + beerToManipulate().getId()))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
    void findBeerByIdAUTH(String user, String pwd) throws Exception{
        mockMvc.perform(get("/api/v1/beer/" + beerToManipulate().getId())
                        .with(httpBasic(user, pwd)))
                .andExpect(status().isOk());
    }

    @Test
    void findBeerByUps() throws Exception{
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
    void findBeerByUpsAUTH(String user, String pwd) throws Exception{
        mockMvc.perform(get("/api/v1/beerUpc/0631234200036")
                        .with(httpBasic(user, pwd)))
                .andExpect(status().isOk());
    }

    @DisplayName("Delete Tests")
    @Nested
    class DeleteTests {
        
        @Test
        void deleteBeer() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToManipulate().getId())
                    .header("Api-Key","spring")
                    .header("Api-Secret", "guru"))
                    .andExpect(status().isOk());
        }
        @Test
        void deleteBeerBadCredentials() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToManipulate().getId())
                            .header("Api-Key","spring")
                            .header("Api-Secret", "guruXXXX"))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        void deleteBeerHttpBasic() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToManipulate().getId())
                    .with(httpBasic("spring", "guru")))
                    .andExpect(status().is2xxSuccessful());
        }
        @Test
        void deleteBeerHttpBasicUserRole() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToManipulate().getId())
                    .with(httpBasic("user", "password")))
                    .andExpect(status().isForbidden());
        }
        @Test
        void deleteBeerHttpBasicCustomerRole() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToManipulate().getId())
                    .with(httpBasic("scott", "tiger")))
                    .andExpect(status().isForbidden());
        }
        @Test
        void deleteBeerNoAuth() throws Exception{
            mockMvc.perform(delete("/api/v1/beer/" + beerToManipulate().getId()))
                    .andExpect(status().isUnauthorized());
        }

    }

}