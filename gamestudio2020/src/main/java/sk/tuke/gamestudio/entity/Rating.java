package sk.tuke.gamestudio.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery( name = "Rating.getAverageRating", query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game=:game"),
        @NamedQuery( name = "Rating.getRating", query = "SELECT r.rating FROM Rating r WHERE r.game=:game and r.player=:player"),
        @NamedQuery( name = "Rating.getIdent", query = "SELECT r.ident FROM Rating r WHERE r.game=:game and r.player=:player")
})
public class Rating implements Comparable<Rating>, Serializable  {
    @Id
    @GeneratedValue
    private int ident;

    private String player;
    private String game;
    private int rating;
    private Date ratedon;

    public Rating() {
    }

    public Rating(String player, String game, int rating, Date ratedon) {
        this.player = player;
        this.game = game;
        this.rating = rating;
        this.ratedon = ratedon;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getRatedon() {
        return ratedon;
    }

    public void setRatedon(Date ratedon) {
        this.ratedon = ratedon;
    }

    @Override
    public int compareTo(Rating o) {
        return o.rating - this.rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating1 = (Rating) o;
        return ident == rating1.ident &&
                rating == rating1.rating &&
                Objects.equals(player, rating1.player) &&
                Objects.equals(game, rating1.game) &&
                Objects.equals(ratedon, rating1.ratedon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ident, player, game, rating, ratedon);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "ident=" + ident +
                ", player='" + player + '\'' +
                ", game='" + game + '\'' +
                ", rating=" + rating +
                ", ratedon=" + ratedon +
                '}';
    }
}
