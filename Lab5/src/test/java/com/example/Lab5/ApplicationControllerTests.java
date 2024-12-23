package com.example.Lab5;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApplicationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AnimalShelterRepository shelterRepository;

	@Autowired
	private AnimalRepository animalRepository;

	@Autowired
	private RatingRepository ratingRepository;

	private AnimalShelter existingShelter;

	@BeforeEach
	void setUp() {
		// Czyszczenie repozytoriów przed każdym testem
		ratingRepository.deleteAll();
		shelterRepository.deleteAll();

		// Inicjalizacja i zapis shelter
		existingShelter = new AnimalShelter();
		existingShelter.setShelterName("Schronisko Testowe");
		existingShelter.setMaxCapacity(50);
		shelterRepository.save(existingShelter);
	}


	// 1. POST /api/animal
	@Test
	void addAnimal_Success() throws Exception {
		// Przygotowanie danych zwierzęcia (bez pola 'shelter')
		Animal animal = new Animal();
		animal.setName("Burek");
		animal.setSpecies("Pies");
		animal.setAge(3);
		animal.setPrice(150.0);
		animal.setWeight(20.5);
		animal.setCondition(AnimalCondition.Zdrowe);
		// 'shelter' nie jest ustawione, ponieważ jest przekazywane jako parametr

		// Konwersja obiektu zwierzęcia do JSON
		String animalJson = objectMapper.writeValueAsString(animal);

		// Wykonanie żądania POST
		mockMvc.perform(post("/api/animal")
						.contentType(MediaType.APPLICATION_JSON)
						.param("shelterId", existingShelter.getId().toString())
						.content(animalJson))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Burek"))
				.andExpect(jsonPath("$.species").value("Pies"))
				.andExpect(jsonPath("$.age").value(3))
				.andExpect(jsonPath("$.price").value(150.0))
				.andExpect(jsonPath("$.weight").value(20.5));

		// Weryfikacja, że zwierzę zostało zapisane w repozytorium
		Optional<Animal> savedAnimal = animalRepository.findByName("Burek");
		assert(savedAnimal.isPresent());
		Animal saved = savedAnimal.get();
		assert(saved.getSpecies().equals("Pies"));
		assert(saved.getAge() == 3);
		assert(saved.getPrice() == 150.0);
		assert(saved.getWeight() == 20.5);
		assert(saved.getCondition() == AnimalCondition.Zdrowe);
		assert(saved.getShelter().getId().equals(existingShelter.getId()));
	}

	@Test
	void addAnimal_InvalidShelterId() throws Exception {
		// Przygotowanie danych zwierzęcia
		Animal animal = new Animal();
		animal.setName("Miau");
		animal.setSpecies("Kot");
		animal.setAge(2);
		animal.setPrice(100.0);
		animal.setWeight(5.0);
		animal.setCondition(AnimalCondition.Chore);

		// Konwersja obiektu zwierzęcia do JSON
		String animalJson = objectMapper.writeValueAsString(animal);

		// Użycie nieistniejącego shelterId
		Long invalidShelterId = existingShelter.getId() + 1000;

		// Wykonanie żądania POST
		mockMvc.perform(post("/api/animal")
						.contentType(MediaType.APPLICATION_JSON)
						.param("shelterId", invalidShelterId.toString())
						.content(animalJson))
				.andExpect(status().isBadRequest());
	}


	@Test
	void addAnimal_ShelterCapacityExceeded() throws Exception {
		// Zakładamy, że schronisko ma maksymalną pojemność 50
		// Dodajemy 50 zwierząt, aby przekroczyć pojemność

		for (int i = 1; i <= 50; i++) {
			Animal animal = new Animal();
			animal.setName("Animal " + i);
			animal.setSpecies("Species " + i);
			animal.setAge(i);
			animal.setPrice(100.0 + i);
			animal.setWeight(10.0 + i);
			animal.setCondition(AnimalCondition.Zdrowe);
			animal.setShelter(existingShelter);
			animalRepository.save(animal);
			existingShelter.getAnimalList().add(animal);
		}

		// Przygotowanie danych nowego zwierzęcia
		Animal newAnimal = new Animal();
		newAnimal.setName("Extra Animal");
		newAnimal.setSpecies("Extra Species");
		newAnimal.setAge(5);
		newAnimal.setPrice(200.0);
		newAnimal.setWeight(15.0);
		newAnimal.setCondition(AnimalCondition.Chore);
		// 'shelter' nie jest ustawione, ponieważ jest przekazywane jako parametr

		// Konwersja obiektu zwierzęcia do JSON
		String newAnimalJson = objectMapper.writeValueAsString(newAnimal);

		// Wykonanie żądania POST, które powinno się nie powieść z powodu przekroczenia pojemności
		mockMvc.perform(post("/api/animal")
						.contentType(MediaType.APPLICATION_JSON)
						.param("shelterId", existingShelter.getId().toString())
						.content(newAnimalJson))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("")); // Możesz dostosować oczekiwaną treść odpowiedzi
	}


	// 2. DELETE /api/animal/{id}
	@Test
	void deleteAnimal_ExistingAnimal_ShouldReturnNoContent() throws Exception {
		// Arrange: Tworzenie i zapisywanie zwierzęcia
		Animal animal = new Animal();
		animal.setName("Burek");
		animal.setSpecies("Pies");
		animal.setAge(3);
		animal.setPrice(150.0);
		animal.setWeight(20.5);
		animal.setCondition(AnimalCondition.Zdrowe);
		animal.setShelter(existingShelter);
		Animal savedAnimal = animalRepository.save(animal);

		// Act & Assert: Wykonanie DELETE i oczekiwanie statusu 204 No Content
		mockMvc.perform(delete("/api/animal/{id}", savedAnimal.getId()))
				.andExpect(status().isNoContent());

		// Weryfikacja, że zwierzę zostało usunięte
		Optional<Animal> deletedAnimal = animalRepository.findById(savedAnimal.getId());
		assertFalse(deletedAnimal.isPresent());
	}

	@Test
	void deleteAnimal_NonExistingAnimal_ShouldReturnNotFound() throws Exception {
		// Arrange: Użycie nieistniejącego id
		Long nonExistingId = 9999L;

		// Act & Assert: Wykonanie DELETE i oczekiwanie statusu 404 Not Found
		mockMvc.perform(delete("/api/animal/{id}", nonExistingId))
				.andExpect(status().isNotFound());
	}

	// 3. GET /api/animal/{id}
	@Test
	void getAnimalById_ExistingAnimal_ShouldReturnAnimal() throws Exception {
		// Arrange: Tworzenie i zapisywanie zwierzęcia
		Animal animal = new Animal();
		animal.setName("Burek");
		animal.setSpecies("Pies");
		animal.setAge(3);
		animal.setPrice(150.0);
		animal.setWeight(20.5);
		animal.setCondition(AnimalCondition.Zdrowe);
		animal.setShelter(existingShelter);
		Animal savedAnimal = animalRepository.save(animal);

		// Act & Assert: Wykonanie GET i oczekiwanie statusu 200 OK z danymi zwierzęcia
		mockMvc.perform(get("/api/animal/{id}", savedAnimal.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(savedAnimal.getId()))
				.andExpect(jsonPath("$.name").value("Burek"))
				.andExpect(jsonPath("$.species").value("Pies"))
				.andExpect(jsonPath("$.age").value(3))
				.andExpect(jsonPath("$.price").value(150.0))
				.andExpect(jsonPath("$.weight").value(20.5))
				.andExpect(jsonPath("$.condition").value("Zdrowe"));
	}

	@Test
	void getAnimalById_NonExistingAnimal_ShouldReturnNotFound() throws Exception {
		// Arrange: Użycie nieistniejącego id
		Long nonExistingId = 9999L;

		// Act & Assert: Wykonanie GET i oczekiwanie statusu 404 Not Found
		mockMvc.perform(get("/api/animal/{id}", nonExistingId))
				.andExpect(status().isNotFound());
	}

	// 4. GET /api/animalshelter/csv
	@Test
	void getSheltersCsv_WithData_ShouldReturnCsv() throws Exception {
		// Arrange: Tworzenie i zapisywanie kolejnego schroniska
		AnimalShelter shelter2 = new AnimalShelter();
		shelter2.setShelterName("Schronisko Drugie");
		shelter2.setMaxCapacity(30);
		shelterRepository.save(shelter2);

		// Act & Assert: Wykonanie GET i oczekiwanie CSV jako odpowiedzi
		String expectedCsv = "id,name,capacity,currentAnimals\n" +
				existingShelter.getId() + "," + existingShelter.getShelterName() + "," +
				existingShelter.getMaxCapacity() + "," + existingShelter.getAnimalList().size() + "\n" +
				shelter2.getId() + "," + shelter2.getShelterName() + "," +
				shelter2.getMaxCapacity() + "," + shelter2.getAnimalList().size() + "\n";

		mockMvc.perform(get("/api/animalshelter/csv"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv;charset=UTF-8"))
				.andExpect(content().string(expectedCsv));
	}

	@Test
	void getSheltersCsv_NoData_ShouldReturnOnlyHeaders() throws Exception {
		// Arrange: Usunięcie wszystkich schronisk
		shelterRepository.deleteAll();

		// Act & Assert: Wykonanie GET i oczekiwanie CSV z samymi nagłówkami
		mockMvc.perform(get("/api/animalshelter/csv"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/csv;charset=UTF-8"))
				.andExpect(content().string("id,name,capacity,currentAnimals\n"));
	}

	// 5. GET /api/sheltermanager
	@Test
	void getAllShelters_WithData_ShouldReturnShelters() throws Exception {
		// Arrange: Tworzenie i zapisywanie kolejnego schroniska
		AnimalShelter shelter2 = new AnimalShelter();
		shelter2.setShelterName("Schronisko Drugie");
		shelter2.setMaxCapacity(30);
		shelterRepository.save(shelter2);

		// Act & Assert: Wykonanie GET i oczekiwanie listy schronisk
		mockMvc.perform(get("/api/sheltermanager"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value(existingShelter.getId()))
				.andExpect(jsonPath("$[0].shelterName").value("Schronisko Testowe"))
				.andExpect(jsonPath("$[0].maxCapacity").value(50))
				.andExpect(jsonPath("$[1].id").value(shelter2.getId()))
				.andExpect(jsonPath("$[1].shelterName").value("Schronisko Drugie"))
				.andExpect(jsonPath("$[1].maxCapacity").value(30));
	}

	@Test
	void getAllShelters_NoData_ShouldReturnEmptyList() throws Exception {
		// Arrange: Usunięcie wszystkich schronisk
		shelterRepository.deleteAll();

		// Act & Assert: Wykonanie GET i oczekiwanie pustej listy
		mockMvc.perform(get("/api/sheltermanager"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(0));
	}

	// 6. POST /api/animalshelter
	@Test
	void createShelter_WithValidData_ShouldReturnCreatedShelter() throws Exception {
		// Arrange: Tworzenie nowego schroniska
		AnimalShelter newShelter = new AnimalShelter();
		newShelter.setShelterName("Schronisko Nowe");
		newShelter.setMaxCapacity(20);

		// Serializacja do JSON
		String newShelterJson = objectMapper.writeValueAsString(newShelter);

		// Act & Assert: Wykonanie POST i oczekiwanie 201 Created z danymi schroniska
		mockMvc.perform(post("/api/animalshelter")
						.contentType(MediaType.APPLICATION_JSON)
						.content(newShelterJson))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.shelterName").value("Schronisko Nowe"))
				.andExpect(jsonPath("$.maxCapacity").value(20))
				.andExpect(jsonPath("$.animalList").isArray())
				.andExpect(jsonPath("$.animalList.length()").value(0)); // Nowo utworzone schronisko nie ma zwierząt
	}


	// 7. DELETE /api/animalshelter/{id}
	@Test
	void deleteShelter_ExistingShelter_ShouldReturnNoContent() throws Exception {
		// Arrange: Tworzenie i zapisywanie schroniska do usunięcia
		AnimalShelter shelterToDelete = new AnimalShelter();
		shelterToDelete.setShelterName("Schronisko Do Usunięcia");
		shelterToDelete.setMaxCapacity(10);
		shelterToDelete = shelterRepository.save(shelterToDelete);

		// Act & Assert: Wykonanie DELETE i oczekiwanie 204 No Content
		mockMvc.perform(delete("/api/animalshelter/{id}", shelterToDelete.getId()))
				.andExpect(status().isNoContent());

		// Weryfikacja, że schronisko zostało usunięte
		Optional<AnimalShelter> deletedShelter = shelterRepository.findById(shelterToDelete.getId());
		assertFalse(deletedShelter.isPresent());
	}

	@Test
	void deleteShelter_NonExistingShelter_ShouldReturnNotFound() throws Exception {
		// Arrange: Użycie nieistniejącego id
		Long nonExistingId = 9999L;

		// Act & Assert: Wykonanie DELETE i oczekiwanie 404 Not Found
		mockMvc.perform(delete("/api/animalshelter/{id}", nonExistingId))
				.andExpect(status().isNotFound());
	}

	// 8. GET /api/animalshelter/{id}/animal
	@Test
	void getAllAnimalsInShelter_ExistingShelterWithAnimals_ShouldReturnAnimals() throws Exception {
		// Arrange: Tworzenie i zapisywanie zwierząt w schronisku
		Animal animal1 = new Animal();
		animal1.setName("Burek");
		animal1.setSpecies("Pies");
		animal1.setAge(3);
		animal1.setPrice(150.0);
		animal1.setWeight(20.5);
		animal1.setCondition(AnimalCondition.Zdrowe); // Użyj właściwej wartości enuma
		animal1.setShelter(existingShelter);
		animalRepository.save(animal1);
		existingShelter.getAnimalList().add(animal1);

		Animal animal2 = new Animal();
		animal2.setName("Misia");
		animal2.setSpecies("Kot");
		animal2.setAge(2);
		animal2.setPrice(100.0);
		animal2.setWeight(5.0);
		animal2.setCondition(AnimalCondition.Chore); // Użyj właściwej wartości enuma
		animal2.setShelter(existingShelter);
		animalRepository.save(animal2);
		existingShelter.getAnimalList().add(animal2);

		// Zapisanie schroniska po dodaniu zwierząt do listy
		shelterRepository.save(existingShelter);

		// Act & Assert: Wykonanie GET i oczekiwanie listy zwierząt
		mockMvc.perform(get("/api/animalshelter/{id}/animal", existingShelter.getId()))
				.andDo(print()) // Dodaj to tymczasowo, aby zobaczyć odpowiedź
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2))) // Użyj hasSize zamiast $.length()
				.andExpect(jsonPath("$[0].name").value("Burek"))
				.andExpect(jsonPath("$[1].name").value("Misia"));
	}


	@Test
	void getAllAnimalsInShelter_ExistingShelterWithNoAnimals_ShouldReturnEmptyList() throws Exception {
		// Arrange: Upewnienie się, że schronisko nie ma zwierząt

		// Act & Assert: Wykonanie GET i oczekiwanie pustej listy
		mockMvc.perform(get("/api/animalshelter/{id}/animal", existingShelter.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void getAllAnimalsInShelter_NonExistingShelter_ShouldReturnNotFound() throws Exception {
		// Arrange: Użycie nieistniejącego id schroniska
		Long nonExistingId = 9999L;

		// Act & Assert: Wykonanie GET i oczekiwanie 404 Not Found
		mockMvc.perform(get("/api/animalshelter/{id}/animal", nonExistingId))
				.andExpect(status().isNotFound());
	}

	// 9. GET /api/animalshelter/{id}/fill
	@Test
	void getShelterFill_ExistingShelter_ShouldReturnFillPercentage() throws Exception {
		// Arrange: Dodanie 25 zwierząt do schroniska o pojemności 50
		for (int i = 1; i <= 25; i++) {
			Animal animal = new Animal();
			animal.setName("Animal " + i);
			animal.setSpecies("Species " + i);
			animal.setAge(i);
			animal.setPrice(100.0 + i);
			animal.setWeight(10.0 + i);
			animal.setCondition(AnimalCondition.Zdrowe);
			animal.setShelter(existingShelter);
			animalRepository.save(animal);
			existingShelter.getAnimalList().add(animal);
		}

		// Act & Assert: Wykonanie GET i oczekiwanie 50.0
		mockMvc.perform(get("/api/animalshelter/{id}/fill", existingShelter.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("50.0"));
	}

	@Test
	void getShelterFill_NonExistingShelter_ShouldReturnNotFound() throws Exception {
		// Arrange: Użycie nieistniejącego id schroniska
		Long nonExistingId = 9999L;

		// Act & Assert: Wykonanie GET i oczekiwanie 404 Not Found
		mockMvc.perform(get("/api/animalshelter/{id}/fill", nonExistingId))
				.andExpect(status().isNotFound());
	}

	@Test
	void getShelterFill_ShelterWithZeroCapacity_ShouldReturnZero() throws Exception {
		// Arrange: Tworzenie schroniska z pojemnością 0
		AnimalShelter zeroCapacityShelter = new AnimalShelter();
		zeroCapacityShelter.setShelterName("Zero Capacity Shelter");
		zeroCapacityShelter.setMaxCapacity(0);
		zeroCapacityShelter = shelterRepository.save(zeroCapacityShelter);

		// Act & Assert: Wykonanie GET i oczekiwanie 0.0
		mockMvc.perform(get("/api/animalshelter/{id}/fill", zeroCapacityShelter.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("0.0"));
	}

	// 10. POST /api/rating
	@Test
	void addRating_WithValidData_ShouldReturnCreatedRating() throws Exception {
		// Arrange: Tworzenie obiektu Rating z istniejącym shelter
		Rating rating = new Rating();
		rating.setRatingValue(5);
		rating.setComment("Great shelter!");
		rating.setRatingDate(LocalDate.now());

		// Ustawienie shelter z existingShelter
		existingShelter.setId(existingShelter.getId());
		rating.setShelter(existingShelter);

		// Serializacja obiektu Rating do JSON
		String ratingJson = objectMapper.writeValueAsString(rating);

		// Act & Assert: Wykonanie POST i oczekiwanie 201 Created z danymi ratingu
		mockMvc.perform(post("/api/rating")
						.contentType(MediaType.APPLICATION_JSON)
						.content(ratingJson))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.ratingValue").value(5))
				.andExpect(jsonPath("$.comment").value("Great shelter!"))
				.andExpect(jsonPath("$.ratingDate").value(LocalDate.now().toString()))
				.andExpect(jsonPath("$.shelter.id").value(existingShelter.getId()))
				.andExpect(jsonPath("$.shelter.shelterName").value(existingShelter.getShelterName()));
	}


	// 11. GET /api/rating
	@Test
	void getAllRatings_WithData_ShouldReturnRatings() throws Exception {
		// Arrange: Tworzenie i zapisywanie kilku ratingów
		Rating rating1 = new Rating();
		rating1.setRatingValue(5);
		rating1.setComment("Excellent!");
		rating1.setRatingDate(LocalDate.now());
		rating1.setShelter(existingShelter);
		ratingRepository.save(rating1);
		shelterRepository.save(existingShelter);

		Rating rating2 = new Rating();
		rating2.setRatingValue(3);
		rating2.setComment("Average shelter.");
		rating2.setRatingDate(LocalDate.now().minusDays(1));
		rating2.setShelter(existingShelter);
		ratingRepository.save(rating2);
		shelterRepository.save(existingShelter);

		// Act & Assert: Wykonanie GET i oczekiwanie listy ratingów
		mockMvc.perform(get("/api/rating"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").value(rating1.getId()))
				.andExpect(jsonPath("$[0].ratingValue").value(5))
				.andExpect(jsonPath("$[0].comment").value("Excellent!"))
				.andExpect(jsonPath("$[0].ratingDate").value(rating1.getRatingDate().toString()))
				.andExpect(jsonPath("$[0].shelter.id").value(existingShelter.getId()))
				.andExpect(jsonPath("$[0].shelter.shelterName").value(existingShelter.getShelterName()))
				.andExpect(jsonPath("$[1].id").value(rating2.getId()))
				.andExpect(jsonPath("$[1].ratingValue").value(3))
				.andExpect(jsonPath("$[1].comment").value("Average shelter."))
				.andExpect(jsonPath("$[1].ratingDate").value(rating2.getRatingDate().toString()));
	}

	@Test
	void getAllRatings_NoData_ShouldReturnEmptyList() throws Exception {
		// Arrange: Usunięcie wszystkich ratingów
		ratingRepository.deleteAll();

		// Act & Assert: Wykonanie GET i oczekiwanie pustej listy
		mockMvc.perform(get("/api/rating"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(0)));
	}

	// 12. DELETE /api/rating/{id}
	@Test
	void deleteRating_ExistingRating_ShouldReturnNoContent() throws Exception {
		// Arrange: Tworzenie i zapisywanie ratingu
		Rating rating = new Rating();
		rating.setRatingValue(4);
		rating.setComment("Good shelter.");
		rating.setRatingDate(LocalDate.now());
		rating.setShelter(existingShelter);
		Rating savedRating = ratingRepository.save(rating);
		shelterRepository.save(existingShelter);

		// Act & Assert: Wykonanie DELETE i oczekiwanie 204 No Content
		mockMvc.perform(delete("/api/rating/{id}", savedRating.getId()))
				.andExpect(status().isNoContent());

		// Weryfikacja, że rating został usunięty
		Optional<Rating> deletedRating = ratingRepository.findById(savedRating.getId());
		assertFalse(deletedRating.isPresent());
	}

	@Test
	void deleteRating_NonExistingRating_ShouldReturnNotFound() throws Exception {
		// Arrange: Użycie nieistniejącego id
		Long nonExistingId = 9999L;

		// Act & Assert: Wykonanie DELETE i oczekiwanie 404 Not Found
		mockMvc.perform(delete("/api/rating/{id}", nonExistingId))
				.andExpect(status().isNotFound());
	}

}
