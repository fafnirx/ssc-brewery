package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Brewery;
import guru.sfg.brewery.security.perm.brewery.BreweryReadPermission;
import guru.sfg.brewery.services.BreweryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BreweryRestController {

    private final BreweryService breweryService;

    @BreweryReadPermission
    @GetMapping("/api/v1/breweries")
    public List<Brewery> getBreweriesJson(){
        return breweryService.getAllBreweries();
    }

}
