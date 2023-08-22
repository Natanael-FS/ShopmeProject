package com.shopme.admin.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.state.StatesRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTest {

	private StatesRepository repo;
	private TestEntityManager entityManager;
	
	@Autowired 
	public StateRepositoryTest(StatesRepository repo, TestEntityManager entityManager) {
		super();
		this.repo = repo;
		this.entityManager = entityManager;
	}
	
	@Test
	public void testCreateState() {
		Integer countryId = 1;
		Country country = entityManager.find(Country.class, countryId);
		
//		Country country = new Country("China", "CN");
		
		State state = new State();
		state.setName("Beijing");
		state.setCountry(country);
		
		State savedStates = repo.save(state);
		
		System.out.println(savedStates);
		System.out.println(savedStates.toString());
		assertThat(savedStates.getCountry().getName()).isEqualTo("China");
	}
	
	@Test
	public void testListStatesByCountry() {
		Integer countryId = 2;
		Country country = entityManager.find(Country.class, countryId);
		List<State> listStates = repo.findByCountryOrderByNameAsc(country);

		listStates.forEach(System.out::println);
		assertThat(listStates.size()).isGreaterThan(0);
	}

	@Test
	public void testUpdateState() {
		Integer stateId = 3;
		String stateName = "Tamil Nadu";
		State state = repo.findById(stateId).get();

		state.setName(stateName);
		State updatedState = repo.save(state);

		assertThat(updatedState.getName()).isEqualTo(stateName);
	}

	@Test
	public void testGetState() {
		Integer stateId = 1;
		Optional<State> findById = repo.findById(stateId);
		assertThat(findById.isPresent());
	}

	@Test
	public void testDeleteState() {
		Integer stateId = 8;
		repo.deleteById(stateId);

		Optional<State> findById = repo.findById(stateId);
		assertThat(findById.isEmpty());		
	}
	
	@Test
	public void tesFindAll() {
		int countryId = 14;
		List<State> listStates = repo.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> result = new ArrayList<>();

		for (State state : listStates) {
			result.add(new StateDTO(state.getId(), state.getName()));
		}

		System.out.println(result.toString());
	}
}
