package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	/*
	 * @Test public void testCreateUserWithOneRole() { Role roleAdmin =
	 * entityManager.find(Role.class, 1); User userNamHM = new
	 * User("nam@codejava.net","nam2020","Nam","Ha Minh");
	 * userNamHM.addRole(roleAdmin);
	 * 
	 * User savedUser = repo.save(userNamHM);
	 * 
	 * assertThat(savedUser.getId()).isGreaterThan(0); }
	 * 
	 * @Test public void testCreateUserWithTwoRole() { User userRavi = new
	 * User("ravi@codejava.net","ravi2020","ravi","kumar");
	 * 
	 * Role roleEditor = new Role(3); Role roleAssistant = new Role(5);
	 * 
	 * 
	 * userRavi.addRole(roleEditor); userRavi.addRole(roleAssistant);
	 * 
	 * User savedUser = repo.save(userRavi);
	 * 
	 * assertThat(savedUser.getId()).isGreaterThan(0); }
	 * 
	 * @Test public void FindUserAll() { Iterable<User> listuser = repo.findAll();
	 * 
	 * listuser.forEach(user -> System.out.println(user)); }
	 */
	
	@Test
	public void tesGetUserById() {
		User userNamUser = repo.findById(1).get();
		System.out.println(userNamUser.toString());
		System.out.println(userNamUser.getPassword());
		assertThat(userNamUser).isNotNull();
		
	}
	
//	@Test
//	public void tesUpdateUserDetails() {
//		User usernamUser = repo.findById(1).get();
//		usernamUser.setEnabled(true);
//		usernamUser.setEmail("namasaas@gmail.com");
//		repo.save(usernamUser);
//	}
//	
//	@Test
//	public void testUpdateUserRoles() {
//		User userravUser = repo.findById(2).get();
//		Role roleEditor = new Role(3);
//		Role roleSalesperson = new Role(2);
//		
//		userravUser.addRole(roleSalesperson);
//		userravUser.getRoles().remove(roleEditor);
//		
//		repo.save(userravUser);
//	}
	
//	@Test
//	public void testDeleteById() {
//		Integer userIdInteger = 2;
//		repo.deleteById(userIdInteger);
//	}
//	
//	@Test
//	public void testGetUserByEmail() {
//		String email = "tes1@gmail.com";
//		User user = repo.getUserByEmail(email);
//		
//		assertThat(user).isNotNull();
//		
//	}
//	
//	@Test
//	public void tesCountById() {
//		Integer id = 1;
//		Long countbyid =  repo.countById(id);
//		
//		assertThat(countbyid).isNotNull().isGreaterThan(0);
//		
//	}
//	
//	@Test
//	public void testDisableUser() {
//		Integer id = 1;
//		repo.updateEnabledStatus(id, true);
//		
//	}
	
	
}
