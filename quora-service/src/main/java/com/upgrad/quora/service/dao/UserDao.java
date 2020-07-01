package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity) {
        try {
            entityManager.persist(userEntity);
            return userEntity;
        } catch (Exception e) {
            return null;
        }
    }

    /* Check to see if the Email entered already exists in DB or not. */
    public Boolean isEmailExists(final String email) {
        try {
            UserEntity singleResult = entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    /* Check to see if the Username entered already exists in DB or not. */
    public Boolean isUsernameExists(final String username) {
        try {
            UserEntity singleResult = entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    /* Fetch user details by putting in username. */
    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Fetch user details by putting in id. */
    public UserEntity getUserById(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* Creates an Auth token in the user_auth DB. */
    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }


    public UserAuthEntity getUserAuthByToken(final String accessToken) {
        try {
            UserAuthEntity authEntity = entityManager.createNamedQuery("userAuthByToken", UserAuthEntity.class)
                    .setParameter("token", accessToken)
                    .getSingleResult();

            return authEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateUserLogoutByToken(final String accessToken, final ZonedDateTime logoutAt) {
        entityManager.createNamedQuery("updateLogoutByToken" )
                .setParameter("token", accessToken)
                .setParameter("logoutAt", logoutAt)
                .executeUpdate();
    }
}
