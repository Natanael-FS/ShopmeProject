package com.shopme.admin.categories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer>{
	public Long countById(Integer id);
	
	@Query("SELECT c FROM Category c WHERE c.name = ?1")
	public Category findByName(String name);

	public Category findByAlias(String alias);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> listRootCategory(Sort sort);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> listRootCategory(Pageable pageable);

	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	@Query("UPDATE Category u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
}
