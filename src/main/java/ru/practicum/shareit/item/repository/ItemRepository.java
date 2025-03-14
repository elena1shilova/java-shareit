package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query(" select i from Item i " +
            " where i.owner.id = ?1 ")
    List<Item> findByOwnerId(Integer userId);

    @Query("SELECT i FROM Item i" +
            " WHERE UPPER(i.name) LIKE %:text% AND i.available = true" +
            " OR UPPER(i.description) LIKE %:text% AND i.available = true")
    List<Item> searchItemsByText(String text);
}
