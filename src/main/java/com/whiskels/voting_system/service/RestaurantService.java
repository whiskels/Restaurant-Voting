package com.whiskels.voting_system.service;

import com.whiskels.voting_system.model.Restaurant;
import com.whiskels.voting_system.repository.RestaurantRepository;
import com.whiskels.voting_system.repository.VoteRepository;
import com.whiskels.voting_system.to.RestaurantTo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.voting_system.util.RepositoryUtil.findById;
import static com.whiskels.voting_system.util.ValidationUtil.checkNotFoundWithId;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;

    @CacheEvict(value = "restaurantTos", allEntries = true)
    public Restaurant create(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        return restaurantRepository.save(restaurant);
    }

    @CacheEvict(value = "restaurantTos", allEntries = true)
    public void delete(int id) {
        checkNotFoundWithId(restaurantRepository.delete(id) != 0, id);
    }

    public Restaurant get(int id) {
        return findById(restaurantRepository, id);
    }

    /**
     * Used to get restaurants with their daily dishes sorted by their daily rating (NOT overall!)
     *
     * @param date on which list is created
     * @return list of RestaurantTo's sorted by their daily rating
     */
    @Cacheable("restaurantTos")
    @Transactional
    public List<RestaurantTo> getAllByDishesDate(LocalDate date) {
        return restaurantRepository.getAllByDishesDate(date).stream()
                .map(r -> new RestaurantTo(
                        r.id(),
                        r.getName(),
                        r.getVotes().stream().filter(vote -> vote.getDate().isEqual(date)).count(),
                        r.getDishes()))
                .sorted(Comparator.comparing(RestaurantTo::getRating).reversed().thenComparing(RestaurantTo::getName))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "restaurantTos", allEntries = true)
    public void update(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        checkNotFoundWithId(restaurantRepository.save(restaurant), restaurant.id());
    }

    @CacheEvict(value = "restaurantTos", allEntries = true)
    @Transactional
    public void enable(int id, boolean enabled) {
        Restaurant restaurant = get(id);
        restaurant.setEnabled(enabled);
    }
}
