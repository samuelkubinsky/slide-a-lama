package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "root";

//    private static final String CREATE_COMMAND = "CREATE TABLE rating (player VARCHAR(64) NOT NULL, rating INTEGER NOT NULL, game VARCHAR(64) NOT NULL, ratedon TIMESTAMP NOT NULL)";
    public static final String UPDATE_RATING = "UPDATE rating SET rating = ?, ratedon = ? WHERE game = ? AND player = ?";
    public static final String INSERT_RATING = "INSERT INTO rating (game, player, rating, ratedon) VALUES (?, ?, ?, ?)";
    public static final String SELECT_RATING = "SELECT game, player, rating, ratedon FROM rating WHERE game = ? AND player = ?";
    public static final String SELECT_AVG_RATING = "SELECT AVG(rating) FROM rating WHERE game = ?";


    @Override
    public void setRating(Rating rating) throws RatingException {
        Rating r = getRatingFromDB(rating.getGame(), rating.getPlayer());

        if (r == null) {
            try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                try(PreparedStatement ps = connection.prepareStatement(INSERT_RATING)) {
                    ps.setString(1, rating.getGame());
                    ps.setString(2, rating.getPlayer());
                    ps.setInt(3, rating.getRating());
                    ps.setDate(4, new Date(rating.getRatedon().getTime()));
                    ps.executeUpdate();
                }
            } catch(SQLException e) {
                throw new RatingException("Error inserting rating", e);
            }
        } else {
            try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                try(PreparedStatement ps = connection.prepareStatement(UPDATE_RATING)) {
                    ps.setInt(1, rating.getRating());
                    ps.setDate(2, new Date(rating.getRatedon().getTime()));
                    ps.setString(3, rating.getGame());
                    ps.setString(4, rating.getPlayer());
                    ps.executeUpdate();
                }
            } catch(SQLException e) {
                throw new RatingException("Error updating rating", e);
            }
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_AVG_RATING)) {
                ps.setString(1, game);

                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch(SQLException e) {
            throw new RatingException("Error loading rating", e);
        }

        return 0;
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        Rating rating = getRatingFromDB(game, player);
        return (rating == null) ? (0) : (rating.getRating());
    }

    private Rating getRatingFromDB(String game, String player) throws RatingException {
        Rating rating = null;

        try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try(PreparedStatement ps = connection.prepareStatement(SELECT_RATING)) {
                ps.setString(1, game);
                ps.setString(2, player);

                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) {
                        rating = new Rating(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getTimestamp(4)
                        );
                    }
                }
            }
        } catch(SQLException e) {
            throw new RatingException("Error loading rating", e);
        }

        return rating;
    }

}
