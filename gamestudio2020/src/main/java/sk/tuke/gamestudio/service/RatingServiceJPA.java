package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) {
        int id = getRatingId(rating.getGame(), rating.getPlayer());

        if (id != -1) {
            rating.setIdent(id);
            entityManager.merge(rating);
        } else {
            entityManager.persist(rating);
        }
    }

    private Integer getRatingId(String game, String player) {
        try {
            Number ident = (Number) entityManager.createNamedQuery("Rating.getIdent")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();

            return ident.intValue();
        } catch (NoResultException e) {
            return -1;
        }
    }

    @Override
    public int getAverageRating(String game) {
        Number rating = (Number) entityManager.createNamedQuery("Rating.getAverageRating")
                .setParameter("game", game)
                .getSingleResult();

        return rating == null ? 0 : rating.intValue();
    }

    @Override
    public int getRating(String game, String player) {
        try {
            Number rating = (Number) entityManager.createNamedQuery("Rating.getRating")
                    .setParameter("game", game)
                    .setParameter("player", player)
                    .getSingleResult();

            return rating.intValue();
        } catch (NoResultException e) {
            return -1;
        }
    }
}
