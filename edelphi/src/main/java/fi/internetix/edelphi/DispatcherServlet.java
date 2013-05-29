package fi.internetix.edelphi;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.smvc.dispatcher.Servlet;

public class DispatcherServlet extends Servlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    GenericDAO.setEntityManager(entityManager);
    try {
      super.service(request, response);
    } finally {
      GenericDAO.setEntityManager(null);
    }
  }
  
  @PersistenceContext
  private EntityManager entityManager;
}
