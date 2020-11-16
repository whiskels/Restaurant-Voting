package com.whiskels.voting_system.web.restaurant;

import com.whiskels.voting_system.model.Dish;
import com.whiskels.voting_system.model.Restaurant;
import com.whiskels.voting_system.service.DishService;
import com.whiskels.voting_system.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.whiskels.voting_system.util.ValidationUtil.assureIdConsistent;
import static com.whiskels.voting_system.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminRestaurantController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public static final String REST_URL = "/admin/restaurants";

    public static final String DISHES_REST_URL = "/{restaurantId}/dishes";

    private final RestaurantService restaurantService;

    private final DishService dishService;

    public AdminRestaurantController(RestaurantService restaurantService, DishService dishService) {
        this.restaurantService = restaurantService;
        this.dishService = dishService;
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id) {
        log.info("get {}", id);
        return restaurantService.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        Restaurant created = restaurantService.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {}", restaurant);
        assureIdConsistent(restaurant, id);
        restaurantService.update(restaurant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        restaurantService.delete(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        restaurantService.enable(id, enabled);
    }

    @PostMapping(value = DISHES_REST_URL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createDish(@PathVariable int restaurantId, @RequestBody Dish dish) {
        log.info("restaurant {} adding dish {}", restaurantId, dish);
        checkNew(dish);
        Dish created = dishService.create(dish, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + DISHES_REST_URL + "/{dishId}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = DISHES_REST_URL + "/{dishId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDish(@RequestBody Dish dish, @PathVariable int dishId, @PathVariable int restaurantId) {
        log.info("update dish {} for restaurant {}", dish, restaurantId);
        assureIdConsistent(dish, dishId);
        dishService.update(dish, restaurantId);
    }

    @DeleteMapping(value = DISHES_REST_URL + "/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDish(@PathVariable int restaurantId, @PathVariable int dishId) {
        log.info("delete dish {} for restaurant {}", dishId, restaurantId);
        dishService.delete(dishId, restaurantId);
    }

    @GetMapping(value = DISHES_REST_URL + "/{dishId}")
    public Dish getDish(@PathVariable int restaurantId, @PathVariable int dishId) {
        log.info("get {}", dishId);
        return dishService.get(dishId, restaurantId);
    }

    @GetMapping(value = DISHES_REST_URL)
    public List<Dish> getAllDishes(@PathVariable int restaurantId) {
        log.info("getAllDishes {}", restaurantId);
        return dishService.getAll(restaurantId);
    }
}
