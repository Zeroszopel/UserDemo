package com.demo.controller;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.demo.dao.UserDAO;
import com.demo.model.User;

import com.demo.dao.CountryDAO;
import com.demo.model.Country;

@WebServlet(name = "UserServlet", urlPatterns = "/users")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private CountryDAO countryDAO;
    private String errors = null;
    public void init() {
        userDAO = new UserDAO();
        countryDAO = new CountryDAO();
        if(this.getServletContext().getAttribute("listCountry")==null)
        	this.getServletContext().setAttribute("listCountry", countryDAO.selectAllCountry());
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
                    insertUser(request, response);
                    break;
                case "edit":
                    updateUser(request, response);
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
                    deleteUser(request, response);
                    break;
                default:
                    listUser(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<User> listUser = userDAO.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Country> listCountry = countryDAO.selectAllCountry();
        request.setAttribute("listCountry", listCountry);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/create.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User existingUser = userDAO.selectUser(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/edit.jsp");
        request.setAttribute("user", existingUser);
        dispatcher.forward(request, response);

    }

    private void insertUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
		/*
		 * String name = request.getParameter("name"); String email =
		 * request.getParameter("email"); String country =
		 * request.getParameter("country"); User newUser = new User(name, email,
		 * country); userDAO.insertUser(newUser); RequestDispatcher dispatcher =
		 * request.getRequestDispatcher("user/create.jsp"); dispatcher.forward(request,
		 * response);
		 */
    	User user = new User();
    	boolean flag = true;
    	Map<String, String> hashMap = new HashMap<String, String>();
    	
    	System.out.println(this.getClass()+" Validation");
    	try {
    		user.setName(request.getParameter("name"));
    		user.setEmail(request.getParameter("email"));
    		user.setPassword(request.getParameter("password"));
    		int idCountry=Integer.parseInt(request.getParameter("country"));
    		user.setCountry(countryDAO.selectCountry(idCountry).getName());
    		String email=request.getParameter("email");
    		System.out.println(this.getClass()+" User info: "+user.getName() );
    		ValidatorFactory validatorFactory=Validation.buildDefaultValidatorFactory();
    		Validator validator= validatorFactory.getValidator();
    		Set<ConstraintViolation<User>> constraintViolations =validator.validate(user);
    		
    		if(!constraintViolations.isEmpty()) {
    			errors= "<ul>";
    			for(ConstraintViolation<User> constraintViolation: constraintViolations) {
    				errors+="<li>"+constraintViolation.getPropertyPath()+" "+constraintViolation.getMessage()+"</li>";
    			}
    			errors+="</ul>";
    			request.setAttribute("user", user);
    			request.setAttribute("errors", errors);
    			request.getRequestDispatcher("user/create.jsp").forward(request, response);
    		} else {
    			if(userDAO.selectUserByEmail(email)!=null) {
    				flag=false;
    				hashMap.put("email", "Email is already used");
    				System.out.println(this.getClass()+" Email is already used");
    			}
    			if(countryDAO.selectCountry(idCountry)==null) {
    				flag=false;
    				hashMap.put("country", "Country invalid");
    				System.out.println(this.getClass()+" Country invalid");
    			
    			}
    			if(flag) {
    				userDAO.insertUser(user);
    				User u = new User();
    				request.setAttribute("user", u);
    				request.getRequestDispatcher("user/create.jsp").forward(request, response);
    			} else {
    				errors = "<ul>";
    				hashMap.forEach(new BiConsumer<String, String>(){
    					@Override
    					public void accept(String keyError,String valueError) {
    						errors+="<li>"+ valueError+"</li>";
    					}
    				});
    				errors+="</ul>";
    				request.setAttribute("user", user);
    				request.setAttribute("errors", errors);
    				System.out.println(this.getClass()+" !constraintViolation.isEmpty()");
    				request.getRequestDispatcher("user/create.jsp").forward(request, response);
    			}
    		}
    	} catch(NumberFormatException e){
    		System.out.println(this.getClass()+" NumberFormatException: User info: "+ user.getName());
    		errors ="<ul>";
    		errors+="<li>"+"Input format wrong"+"</li>";
    		errors+="</ul>";
    		request.setAttribute("user", user);
    		request.setAttribute("errors", errors);
    		request.getRequestDispatcher("user/create.jsp").forward(request, response);;
    	}
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
		/*
		 * int id = Integer.parseInt(request.getParameter("id")); String name =
		 * request.getParameter("name"); String email = request.getParameter("email");
		 * String country = request.getParameter("country"); String password =
		 * request.getParameter("password"); User book = new User(id, name, email,
		 * country, password); userDAO.updateUser(book); User existingUser =
		 * userDAO.selectUser(id); List<Country> listCountry =
		 * countryDAO.selectAllCountry(); request.setAttribute("listCountry",
		 * listCountry); RequestDispatcher dispatcher =
		 * request.getRequestDispatcher("user/edit.jsp"); request.setAttribute("user",
		 * existingUser); dispatcher.forward(request, response);
		 */
    	User user = new User();
    	boolean flag = true;
    	Map<String, String> hashMap = new HashMap<String, String>();
    	
    	System.out.println(this.getClass()+" Validation");
    	try {
    		int id = Integer.parseInt(request.getParameter("id"));
    		user.setId(id);
    		user.setName(request.getParameter("name"));
    		user.setEmail(request.getParameter("email"));
    		user.setPassword(request.getParameter("password"));
    		int idCountry=Integer.parseInt(request.getParameter("country"));
    		user.setCountry(countryDAO.selectCountry(idCountry).getName());
    		String email=request.getParameter("email");
    		System.out.println(this.getClass()+" User info: "+user.getName() );
    		ValidatorFactory validatorFactory=Validation.buildDefaultValidatorFactory();
    		Validator validator= validatorFactory.getValidator();
    		Set<ConstraintViolation<User>> constraintViolations =validator.validate(user);
    		
    		if(!constraintViolations.isEmpty()) {
    			errors= "<ul>";
    			for(ConstraintViolation<User> constraintViolation: constraintViolations) {
    				errors+="<li>"+constraintViolation.getPropertyPath()+" "+constraintViolation.getMessage()+"</li>";
    			}
    			errors+="</ul>";
    			request.setAttribute("user", user);
    			request.setAttribute("errors", errors);
    			request.getRequestDispatcher("user/edit.jsp").forward(request, response);
    		} else {
    			if(userDAO.selectUserByEmail(email)!=null) {
    				flag=false;
    				hashMap.put("email", "Email is already used");
    				System.out.println(this.getClass()+" Email is already used");
    			}
    			if(countryDAO.selectCountry(idCountry)==null) {
    				flag=false;
    				hashMap.put("country", "Country invalid");
    				System.out.println(this.getClass()+" Country invalid");
    			
    			}
    			if(flag) {
    				userDAO.updateUser(user);
    				User u = new User();
    				u=userDAO.selectUser(id);
    				request.setAttribute("user", u);
    				request.getRequestDispatcher("user/edit.jsp").forward(request, response);
    			} else {
    				errors = "<ul>";
    				hashMap.forEach(new BiConsumer<String, String>(){
    					@Override
    					public void accept(String keyError,String valueError) {
    						errors+="<li>"+ valueError+"</li>";
    					}
    				});
    				errors+="</ul>";
    				request.setAttribute("user", user);
    				request.setAttribute("errors", errors);
    				System.out.println(this.getClass()+" !constraintViolation.isEmpty()");
    				request.getRequestDispatcher("user/edit.jsp").forward(request, response);
    			}
    		}
    	} catch(NumberFormatException e){
    		System.out.println(this.getClass()+" NumberFormatException: User info: "+ user.getName());
    		errors ="<ul>";
    		errors+="<li>"+"Input format wrong"+"</li>";
    		errors+="</ul>";
    		request.setAttribute("user", user);
    		request.setAttribute("errors", errors);
    		request.getRequestDispatcher("user/edit.jsp").forward(request, response);;
    	}
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        userDAO.deleteUser(id);

        List<User> listUser = userDAO.selectAllUsers();
        request.setAttribute("listUser", listUser);
        RequestDispatcher dispatcher = request.getRequestDispatcher("user/list.jsp");
        dispatcher.forward(request, response);
    }
}