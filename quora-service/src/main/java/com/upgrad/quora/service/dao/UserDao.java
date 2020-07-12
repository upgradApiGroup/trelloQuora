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

  /** User Creation method
   *
   * @param userEntity - userEntity object is passed into this method.
   *
   * */
  public UserEntity createUser(UserEntity userEntity) {
    try {
      entityManager.persist(userEntity);
      return userEntity;
    } catch (Exception e) {
      return null;
    }
  }

  /** Check to see if the Email entered already exists in DB or not.
   *
   * @param email - email attribute value is passed in this method.
   * @return false - if the email does not exist in the DB this method returns false.
   *
   * */
  public Boolean isEmailExists(final String email) {
    try {
      UserEntity singleResult = entityManager.createNamedQuery("userByEmail", UserEntity.class)
          .setParameter("email", email).getSingleResult();
      return true;
    } catch (NoResultException nre) {
      return false;
    }
  }

  /** Check to see if the Username entered already exists in DB or not.
   *
   * @param username - username attribute value is passed in this method.
   * @return false - if the username does not exist in the DB this method returns false.
   *
   * */
  public Boolean isUsernameExists(final String username) {
    try {
      UserEntity singleResult = entityManager.createNamedQuery("userByUsername", UserEntity.class)
          .setParameter("username", username).getSingleResult();
      return true;
    } catch (NoResultException nre) {
      return false;
    }
  }

  /** Fetch user details by putting in username.
   *
   * @param username - username attribute value is passed in this method.
   *
   * */
  public UserEntity getUserByUsername(final String username) {
    try {
      return entityManager.createNamedQuery("userByUsername", UserEntity.class)
          .setParameter("username", username).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Fetch user details by putting in email.
   *
   * @param email - email attribute value is passed in this method.
   *
   * */
  public UserEntity getUserByEmail(final String email) {
    try {
      return entityManager.createNamedQuery("userByEmail", UserEntity.class)
          .setParameter("email", email).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Fetch user details by putting in id.
   *
   * @param uuid - uuid attribute value is passed in this method.
   *
   * */
  public UserEntity getUserById(final String uuid) {
    try {
      return entityManager.createNamedQuery("userByUuid", UserEntity.class)
          .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Creates an Auth token in the user_auth DB.
   *
   * @param userAuthEntity - userAuthEntity object value is passed in this method.
   * @return userAuthEntity
   *
   * */
  public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
    entityManager.persist(userAuthEntity);
    return userAuthEntity;
  }

  /** Creates an Auth token in the user_auth DB.
   *
   * @param accessToken - accessToken value is passed in this method.
   * @return authEntity
   *
   * */
  public UserAuthEntity getUserAuthByToken(final String accessToken) {
    try {
      UserAuthEntity authEntity = entityManager
          .createNamedQuery("userAuthByToken", UserAuthEntity.class)
          .setParameter("token", accessToken)
          .getSingleResult();

      return authEntity;
    } catch (NoResultException nre) {
      return null;
    }
  }

  /** Creates an Auth token in the user_auth DB.
   *
   * @param accessToken - accessToken object value is passed to this method.
   * @param logoutAt - logout time object is passed to this method
   *
   * */
  public void updateUserLogoutByToken(final String accessToken, final ZonedDateTime logoutAt) {
    entityManager.createNamedQuery("updateLogoutByToken")
        .setParameter("token", accessToken)
        .setParameter("logoutAt", logoutAt)
        .executeUpdate();
  }
}
