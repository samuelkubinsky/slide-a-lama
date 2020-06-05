package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingServiceJDBCTest {

    private final RatingService ratingService = new RatingServiceJDBC();
    private final String gameName = "SLIDE-A-LAMA";
    private final String playerName = "MAC";
    private final int rating = 4;

    @Test
    void setRating() throws RatingException {
        ratingService.setRating(new Rating(playerName, gameName, Math.round(rating), new java.util.Date()));
        assertEquals(4, ratingService.getRating(gameName, playerName));
    }

    @Test
    void getAverageRating() throws RatingException {
        String gameName = "OKP";

        String playerName = "LUNA";
        int rating = 4;
        ratingService.setRating(new Rating(playerName, gameName, Math.round(rating), new java.util.Date()));

        playerName = "MIREZ";
        rating = 2;
        ratingService.setRating(new Rating(playerName, gameName, Math.round(rating), new java.util.Date()));

        playerName = "KENNY";
        rating = 1;
        ratingService.setRating(new Rating(playerName, gameName, Math.round(rating), new java.util.Date()));

        assertEquals(2, ratingService.getAverageRating(gameName));
    }

    @Test
    void getRating() throws RatingException {
        assertEquals(4, ratingService.getRating(gameName, playerName));
    }
}