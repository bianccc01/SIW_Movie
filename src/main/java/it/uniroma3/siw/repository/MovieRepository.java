package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> {
	
	public List<Movie> findByTitleContainingIgnoreCase(String title);

	public List<Movie> findByYear(int year);

	public boolean existsByTitleAndYear(String title, int year);	
}