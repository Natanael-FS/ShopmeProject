package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingandSortingHelper;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> listAll() {
//		 return (List<User>)userRepository.findAll();	
	 return (List<User>)userRepository.findAll(Sort.by("firstName").ascending());	
	}
	
	
	public void listByPage(int pageNum, PagingandSortingHelper helper){
		helper.listEntities(pageNum, USERS_PER_PAGE, userRepository);
	}
	
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepository.findAll();
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		
		if (isUpdatingUser) {
				User existingUser = userRepository.findById(user.getId()).get();
				
				if (user.getPassword().isEmpty()) {
					user.setPassword(existingUser.getPassword());
				}
				
				else {
					encodePassword(user);
				}
		}
		else {
			encodePassword(user);
		}
		
		return userRepository.save(user);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public boolean isEmailUnique(Integer id, String email) { 
	 User userByEmail = userRepository.getUserByEmail(email); 
	 
	 if(userByEmail == null) return true;
	 	 
	 boolean isCreatingNew = (id==null);
	 
	 if(isCreatingNew ) {
		 if(userByEmail != null) return false;
	 }else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
	 return true;
	}
	
	public User get(Integer id ) throws UserNotFoundException{
		try {
			return userRepository.findById(id).get();			
		}
		catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any use with ID" + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepository.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("could not find any user with id " +id);
		}
		
		userRepository.deleteById(id);
		
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled)
	{
		userRepository.updateEnabledStatus(id, enabled);
	}

	public User getByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}

	public User updateAccount(User userInForm) {
		User userInDB = userRepository.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}

		if (userInForm.getPhotos() != null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());

		return userRepository.save(userInDB);
	}
	
}
