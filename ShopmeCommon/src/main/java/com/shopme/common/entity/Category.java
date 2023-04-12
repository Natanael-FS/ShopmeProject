package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes.Name;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "categories")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@Column(name = "alias", nullable = false, unique = true)
	private String alias;
	
	@Column(name = "image", nullable = false)
	private String image;

	private Boolean enabled;

	
	@OneToOne
	@JoinColumn(name= "parent_id")
	private Category parent;
	
	@OneToMany( mappedBy = "parent")
	private Set<Category> children = new HashSet<>();
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> childrenCategories) {
		this.children= childrenCategories;
	}

	@Transient
	public String getImagePath() {
		if(id == null)
			return "/images/image-thumbnail.png";
//		System.out.println("/category-images/"+this.id+"/"+this.image);
		return "/category-images/"+this.id+"/"+this.image;
	}
	
	public Category() {
	}
	
	
	public Category(Integer id) {
		this.id = id;
	}
	
	public static Category copyIdAndName(Category category) {
		Category copycategory = new Category();
		copycategory.setId(category.getId());
		copycategory.setName(category.getName());
		return copycategory;
	}
	
	public static Category copyIdAndName(Integer id, String name) {
		Category copycategory = new Category();
		copycategory.setId(id);
		copycategory.setName(name);
		return copycategory;
	}
	
	public static Category copyFull(Category category) {
		Category copycategory = new Category();
		copycategory.setId(category.getId());
		copycategory.setName(category.getName());
		copycategory.setAlias(category.getAlias());
		copycategory.setImage(category.getImage());
		copycategory.setEnabled(category.getEnabled());
		
		return copycategory;
	}
	
	public static Category copyFull(Category category, String name) {
		Category copycategory = Category.copyFull(category);
		copycategory.setName(name);
		
		return copycategory;
	}
	
	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}

	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}
		
	public Category(Integer id, String name, String alias) {
		super();
		this.id = id;
		this.name = name;
		this.alias = alias;
	}

//	public Category(Integer id, String name, String alias, String image, Boolean enabled, Category parent,
//			Set<Category> children) {
//		this.id = id;
//		this.name = name;
//		this.alias = alias;
//		this.image = image;
//		this.enabled = enabled;
//		this.parent = parent;
//		this.children = children;
//	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", alias=" + alias + ", image=" + image + ", enabled="
				+ enabled + ", parent=" + parent + ", children=" + children + "]";
	}

	
}
