package com.shopme.admin.categories;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.apache.poi.poifs.property.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoriesService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 1;
			
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> findRoot(String sortDir) {
		Sort sort = Sort.by("name");

		if (sortDir == null || sortDir.isEmpty()) {
			sort = sort.ascending();
		}else if (sortDir.equals("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equals("desc")) {
			sort = sort.descending();
		}
		
		List<Category> rootCategories = repo.listRootCategory(sort);
		
		for (Category category : rootCategories) {
			System.out.println(category.getId()+" || "+category.getName());
		}
		
		return listHierarchicalCategories(rootCategories, sortDir);
	}

	public List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for (Category category : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(category));
			
			Set<Category> childrenCategories = sortSubCategories(category.getChildren(), sortDir);
			
			for (Category subCategory : childrenCategories) {
				String name = "--"+subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		return hierarchicalCategories;
	}
	
	
	public List<Category> listCategoriesUsedInForm() {	
		List<Category> findCategoriesInDb = new ArrayList<>();

//		Iterable<Category> findCategories = repo.listRootCategory(Sort.by("name").ascending());
		Iterable<Category> findCategories = repo.findAll();
		System.out.println("findCat - "+findCategories.toString());
		for (Category category : findCategories) {
			if (category.getParent() == null) {
				findCategoriesInDb.add(Category.copyIdAndName(category));
				
				Set<Category> children = sortSubCategories(category.getChildren());
				for (Category subCategory: children) {
					String name = "--"+subCategory.getName();
					findCategoriesInDb.add(Category.copyIdAndName(subCategory.getId(), name));

					listSubCategoriesUsedInForm(findCategoriesInDb, subCategory, 1);
					
				}
			}
		}
		System.out.println("findCategoriesInDb - "+findCategoriesInDb.toString());

		return findCategoriesInDb;
	}
	
	private void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, 
			Category parent, int subLevel){
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());

		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {				
				name += "--";
			}
			name += subCategory.getName();
			System.out.println("tes = " + name );

			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));

			listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
		}		
	}

/*	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> findCategoriesInDb = new ArrayList<>();

//		Iterable<Category> findCategories = repo.listRootCategory(Sort.by("name").ascending());
		Iterable<Category> findCategories = repo.findAll();
		System.out.println("findCat - "+findCategories.toString());
		for (Category category : findCategories) {
//			if (category.getParent() == null) {
				findCategoriesInDb.add(Category.copyIdAndName(category));
				
				Set<Category> children = sortSubCategories(category.getChildren());
				for (Category subCategory: children) {
					String name = "--"+subCategory.getName();
					findCategoriesInDb.add(Category.copyIdAndName(subCategory.getId(), name));

					listSubCategoriesUsedInForm(findCategoriesInDb, subCategory, 1);
					
				}
//			}
		}
		System.out.println("findCategoriesInDb - "+findCategoriesInDb.toString());

		return findCategoriesInDb;
	}
	
	void listSubCategoriesUsedInForm(List<Category> findCategoriesInDb, Category parent, int subLevel) {
		Set<Category> children = sortSubCategories(parent.getChildren());
		int subNewLvl = subLevel + 1;
		String name = "";
		
		for (Category subCategory: children) {
			for (int i = 0; i < subNewLvl; i++) {
//				name = "--"+name; // += -> name = name + "--"
//				name = "--"+name; // += -> name = name + "--"
				name += "--"; // += -> name = name + "--"
			}
			name += subCategory.getName(); 
			System.out.println("tes = " + name );
			findCategoriesInDb.add(Category.copyIdAndName(subCategory.getId(), name));
			listSubCategoriesUsedInForm(findCategoriesInDb, subCategory, subNewLvl);
		}
	}
*/
	void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, 
			int subLevel, String sortDir){
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel + 1;
		for (Category subCategory : children) {
			String name= "";
			for (int i = 0; i < newSubLevel ; i++) {
				name += "--";
			}
			name += subCategory.getName();
			
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
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
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			
			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});	
		sortedChildren.addAll(children);
/*		
		System.out.println("\n\nSubCat");
		for (Category category : sortedChildren) {
			System.out.println(category.getId()+" || "+category.getName());
		}				 
 */
	
		return sortedChildren;
	}
	
	
	public List<Category> listByPage(CategoryPageInfo pageInfo,int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");
	
		if (sortDir.equals("asc")) {
			sort = sort.ascending();
		}else if (sortDir.equals("desc")) {
			sort = sort.descending();			
		}

		Pageable pageable = PageRequest.of(pageNum -1, ROOT_CATEGORIES_PER_PAGE, sort);
		
		Page<Category> pageCategories = null;
		if (keyword != null && !keyword.isEmpty()) {
			pageCategories = repo.search(keyword, pageable); 
		}else {
			pageCategories = repo.listRootCategory(pageable);			
		}

		List<Category> rootCategories =	pageCategories.getContent();
//		System.out.println("peak ::"+rootCategories.toString());
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		if (keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			for (Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			return searchResult;
		}else {
			return listHierarchicalCategories(rootCategories, sortDir);	
		}
	}
	
	public List<Category> listAll() {
		List<Category> categoryList = new ArrayList<>();
		repo.findAll().forEach(categoryList::add);
		return categoryList;	
	}
	
	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent!=null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}

		return repo.save(category);
	}

/*
	public Category save(Category category) {
		boolean isUpdatingcategory = (category.getId() != null);
		
		if (isUpdatingcategory) {
			Category existingcategory = repo.findById(category.getId()).get();		
		}
		
		return repo.save(category);
	}
*/
	
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
}