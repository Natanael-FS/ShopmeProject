package com.shopme.admin.categories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

import ch.qos.logback.core.joran.conditional.ElseAction;

@Service
@Transactional
public class CategoriesService {
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> findRoot() {
		List<Category> rootCategories = repo.listRootCategory();
		return listHierarchicalCategories(rootCategories);
	}

	public List<Category> listHierarchicalCategories(List<Category> rootCategories) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for (Category category : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(category));
			
			Set<Category> childrenCategories = category.getChildren();
			
			for (Category subCategory : childrenCategories) {
				String name = "--"+subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1);
			}
		}
		return hierarchicalCategories;
	}
	
	public List<Category> listAll() {
		List<Category> categoryList = new ArrayList<>();
		repo.findAll().forEach(categoryList::add);
		return categoryList;	
		}
	
	public Category save(Category category) {
		boolean isUpdatingcategory = (category.getId() != null);
		
		if (isUpdatingcategory) {
			Category existingcategory = repo.findById(category.getId()).get();		
		}
		
		return repo.save(category);
	}
	
	
	public Category getCategory(Integer id) {
		Category category = repo.findById(id).get();
		return category;
	}
	
	public void deleteCategory(Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);
		if (countById == null || countById == 0) {
			throw new CategoryNotFoundException("could not find any category with id " +id);
		}
		
		 repo.deleteById(id);
	}
	
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) throws CategoryNotFoundException {
		repo.updateEnabledStatus(id, enabled);
	}
	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> findCategoriesInDb = new ArrayList<>();

		Iterable<Category> findCategories = repo.findAll();
		
		for (Category category : findCategories) {
			if (category.getParent() == null) {
				findCategoriesInDb.add(Category.copyIdAndName(category));
				
				Set<Category> children = category.getChildren();
				for (Category subCategory: children) {
					String name = "--"+subCategory.getName();
					findCategoriesInDb.add(Category.copyIdAndName(subCategory.getId(), name));

					listSubCategoriesUsedInForm(findCategoriesInDb, subCategory, 1);
				}
			}
		}
		return findCategoriesInDb;
	}
	
	void listSubCategoriesUsedInForm(List<Category> findCategoriesInDb, Category parent, int subLevel) {
		Set<Category> children = parent.getChildren();
		int subNewLvl = subLevel + 1;
		String name = "";
		
		for (Category subCategory: children) {
			for (int i = 0; i < subNewLvl; i++) {
				name = "--"+name; // += -> name = name + "--"
			}
			name += subCategory.getName(); 

			findCategoriesInDb.add(Category.copyIdAndName(subCategory.getId(), name));
			listSubCategoriesUsedInForm(findCategoriesInDb, subCategory, subNewLvl);
		}
	}
	
	void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel){
		Set<Category> children = parent.getChildren();
		int newSubLevel = subLevel + 1;
		for (Category subCategory : children) {
			String name= "";
			for (int i = 0; i < newSubLevel ; i++) {
				name += "--";
			}
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel);
		}
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			}else {
				Category categoryByAliasCategory = repo.findByAlias(alias);
				if (categoryByAliasCategory != null) {
					return "DuplicateAlias";
				}
			}
		}else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			
			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
			
		}
			
		return "OK";
	}
}