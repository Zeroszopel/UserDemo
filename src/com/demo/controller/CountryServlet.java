package com.demo.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.dao.CountryDAO;
import com.demo.model.Country;
@WebServlet(name="CountryServlet", urlPatterns= {"/country"})
public class CountryServlet extends HttpServlet{
	   private static final long serialVersionUID = 1L;
	    private CountryDAO CountryDAO;

	    public void init() {
	        CountryDAO = new CountryDAO();
	    }

	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        String action = request.getParameter("action");
	        if (action == null) {
	            action = "";
	        }
	        try {
	            switch (action) {
	                case "create":
	                    insertCountry(request, response);
	                    break;
	                case "edit":
	                    updateCountry(request, response);
	                    break;
	            }
	        } catch (SQLException ex) {
	            throw new ServletException(ex);
	        }
	    }

	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        String action = request.getParameter("action");
	        if (action == null) {
	            action = "";
	        }

	        try {
	            switch (action) {
	                case "create":
	                    showNewForm(request, response);
	                    break;
	                case "edit":
	                    showEditForm(request, response);
	                    break;
	                case "delete":
	                    deleteCountry(request, response);
	                    break;
	                default:
	                    listCountry(request, response);
	                    break;
	            }
	        } catch (SQLException ex) {
	            throw new ServletException(ex);
	        }
	    }

	    private void listCountry(HttpServletRequest request, HttpServletResponse response)
	            throws SQLException, IOException, ServletException {
	    	
	        List<Country> listCountry = CountryDAO.selectAllCountry();
	        request.setAttribute("listCountry", listCountry);
	        
	        RequestDispatcher dispatcher = request.getRequestDispatcher("country/list.jsp");
	        dispatcher.forward(request, response);
	    }

	    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        RequestDispatcher dispatcher = request.getRequestDispatcher("country/create.jsp");
	        dispatcher.forward(request, response);
	    }

	    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
	            throws SQLException, ServletException, IOException {
	        int id = Integer.parseInt(request.getParameter("id"));
	        Country existingCountry = CountryDAO.selectCountry(id);
	        RequestDispatcher dispatcher = request.getRequestDispatcher("country/edit.jsp");
	        request.setAttribute("country", existingCountry);
	        dispatcher.forward(request, response);

	    }

	    private void insertCountry(HttpServletRequest request, HttpServletResponse response)
	            throws SQLException, IOException, ServletException {
	        String name = request.getParameter("name");
	        Country newCountry = new Country(name);
	        CountryDAO.insertCountry(newCountry);
	        RequestDispatcher dispatcher = request.getRequestDispatcher("country/create.jsp");
	        dispatcher.forward(request, response);
	    }

	    private void updateCountry(HttpServletRequest request, HttpServletResponse response)
	            throws SQLException, IOException, ServletException {
	        int id = Integer.parseInt(request.getParameter("id"));
	        String name = request.getParameter("name");
	        System.out.println(name);
	        Country book = new Country(id, name);
	        CountryDAO.updateCountry(book);
	        Country existingCountry = CountryDAO.selectCountry(id);
	        RequestDispatcher dispatcher = request.getRequestDispatcher("country/edit.jsp");
	        request.setAttribute("country", existingCountry);
	        dispatcher.forward(request, response);
	    }

	    private void deleteCountry(HttpServletRequest request, HttpServletResponse response)
	            throws SQLException, IOException, ServletException {
	        int id = Integer.parseInt(request.getParameter("id"));
	        CountryDAO.deleteCountry(id);

	        List<Country> listCountry = CountryDAO.selectAllCountry();
	        request.setAttribute("listCountry", listCountry);
	        RequestDispatcher dispatcher = request.getRequestDispatcher("country/list.jsp");
	        dispatcher.forward(request, response);
	    }
}
