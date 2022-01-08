package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Spring RestController takes care of mapping request data to the defined request handler method
@RestController

//the annotation is used to map web requests to Spring Controller methods
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    //Annotation for mapping HTTP GET requests onto specific handler methods
    @GetMapping

    //tells a controller that the object returned is automatically serialized into JSON and passed back into the HttpResponse object
    @ResponseBody
    public List<Player> getAll(@RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "race", required = false) Race race,
                               @RequestParam(value = "profession", required = false) Profession profession,
                               @RequestParam(value = "after", required = false) Long after,
                               @RequestParam(value = "before", required = false) Long before,
                               @RequestParam(value = "banned", required = false) Boolean banned,
                               @RequestParam(value = "minExperience", required = false) Integer minExperience,
                               @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                               @RequestParam(value = "minLevel", required = false) Integer minLevel,
                               @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                               @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                               @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder playerOrder) {

        //The Pageable implementation represents a set of pages to be printed.
        //The Pageable object returns the total number of pages in the set as well as the PageFormat and Printable for a specified page.
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(playerOrder.getFieldName()));

        //Returns a Page of entities matching the given Specification.
        return playerService.findAllPlayers(
                Specification.where(playerService.filterByName(name))
                        .and(playerService.filterByTitle(title))
                        .and(playerService.filterByRace(race))
                        .and(playerService.filterByProfession(profession))
                        .and(playerService.filterByExperience(minExperience, maxExperience))
                        .and(playerService.filterByLevel(minLevel, maxLevel))
                        .and(playerService.filterByBirthday(after, before))
                        .and(playerService.filterByBanned(banned)),
                pageable).getContent();
    }

    @GetMapping("/count")
    @ResponseBody
    public Long getCount(@RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "race", required = false) Race race,
                         @RequestParam(value = "profession", required = false) Profession profession,
                         @RequestParam(value = "after", required = false) Long after,
                         @RequestParam(value = "before", required = false) Long before,
                         @RequestParam(value = "banned", required = false) Boolean banned,
                         @RequestParam(value = "minExperience", required = false) Integer minExperience,
                         @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                         @RequestParam(value = "minLevel", required = false) Integer minLevel,
                         @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        //Returns the number of instances that the given Specification will return.
        return playerService.getCountPlayers(Specification.where(playerService.filterByName(name))
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel))
                .and(playerService.filterByBirthday(after, before))
                .and(playerService.filterByBanned(banned))
        );
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        //returns a ResponseEntity with the given body and the status set to OK.
        Player player1 = playerService.createPlayer(player);
        if (player1 == null) {
            return new ResponseEntity<Player>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(player1);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Player> getPlayerById(@PathVariable(name = "id") Long id) {
        if (id <= 0) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player player1 = playerService.getPlayerById(id);
        if (player1 == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @PostMapping("/{id}")
    @ResponseBody
    //                                         the @PathVariable annotation can be used to handle template variables in the request
    //                                         URI mapping, and set them as method parameters
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player player) {
        if (id <= 0 || invalidPlayer(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player player1 = playerService.updatePlayer(id, player);
        if (player1 == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deletePlayer(@PathVariable("id") Long id) {
        playerService.deletePlayer(id);
    }



    private boolean invalidPlayer(Player player) {
        return (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10000000))
                || (player.getBirthday() != null && player.getBirthday().getTime() < 0);

    }


}
