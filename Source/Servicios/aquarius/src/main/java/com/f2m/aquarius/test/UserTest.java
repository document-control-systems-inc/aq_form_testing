package com.f2m.aquarius.test;

import com.f2m.aquarius.beans.ServiceResponse;
import com.f2m.aquarius.controller.UserController;

public class UserTest {

	public static void main(String[] args) {
		UserController user = new UserController();
		ServiceResponse response = user.login("demo", "demo");
		System.out.println("response: " + response.getStatus());
	}

}
