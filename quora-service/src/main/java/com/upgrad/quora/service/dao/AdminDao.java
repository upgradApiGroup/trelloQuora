package com.upgrad.quora.service.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AdminDao {

  @PersistenceContext
  private EntityManager entityManager;

  /** Delete user by uuid method
   *
   * @param uuid - uuid of the user is sent to this method for the user to be deleted.
   *
   * */
  public void deleteUserByUuid(final String uuid) {
    try {
      entityManager.createNamedQuery("deleteUserById")
          .setParameter("uuid", uuid)
          .executeUpdate();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
