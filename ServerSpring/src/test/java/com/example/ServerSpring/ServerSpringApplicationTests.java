package com.example.ServerSpring;

import com.example.ServerSpring.model.Categorys.Categories;
import com.example.ServerSpring.model.Categorys.CategoriesDao;
import com.example.ServerSpring.model.Roles.Roles;
import com.example.ServerSpring.model.Roles.RolesDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest

class ServerSpringApplicationTests {

	@Autowired
	RolesDao rolesDao = new RolesDao();

	@Autowired
	CategoriesDao categoriesDao = new CategoriesDao();

	@Test
	void contextLoads() {
		Categories category = new Categories();
		category.setName("glavnoe");
		categoriesDao.save(category);
	}

	//@Test
	void getAllRoles(){
		List<Roles> roles = rolesDao.getAllRoles();
		for (Roles list : roles){
			rolesDao.delete(list);
		}
		System.out.println("HELLO");
		System.out.println(roles);
	}

}
