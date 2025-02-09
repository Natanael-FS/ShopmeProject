package com.shopme.admin.state;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@Repository
public interface StatesRepository extends CrudRepository<State, Integer>{
	public List<State> findByCountryOrderByNameAsc(Country country);
}
