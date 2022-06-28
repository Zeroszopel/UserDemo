package com.demo.model;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.Length;

public class User {
    protected int id;
    protected String name;
    protected String email;
    protected String country;
    protected String password;
    

	public User() {}

    public User(String name, String email, String country,String password) {
        super();
        this.name = name;
        this.email = email;
        this.country = country;
        this.password = password;
    }

    public User(int id, String name, String email, String country,String password) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.country = country;

        this.password = password;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    @NotEmpty(message="Name cannot be empty")
    @Pattern(regexp="([A-Z][a-z]+)(\\s[A-Z][a-z]+)*",message=": First letter of each word must be uppercase")
    @Length(min=3, max=50,message="Must be 3-50 Character")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @NotEmpty(message="Email cannot be empty")
    @Pattern(regexp="[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",message=": '<'name'>'@domainname>.<domain>'")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @NotEmpty(message="Country cannot be empty")
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    @NotEmpty(message="Password cannot be empty")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message=" must have at least 8 characters that contain at least 1 letter, 1 number, 1 special character")
    public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}