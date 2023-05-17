package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Service
public class MovieService {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private ImageRepository imageRepository;


	@Transactional
	public Movie setDirectorToMovie(Long directorId,Long movieId) {
		Artist director = this.artistRepository.findById(directorId).get();
		Movie movie = this.movieRepository.findById(movieId).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);

		return movie;
	}

	@Transactional
	public void newImagesMovie(MultipartFile[] files, Movie movie) throws IOException {

		for (MultipartFile file : files) {
			byte[] imageData = file.getBytes();
			String imageName = file.getOriginalFilename();

			Image image = new Image();
			image.setName(imageName);
			image.setBytes(imageData);


			movie.addImage(image);
		}
		
		this.imageRepository.saveAll(movie.getImages());
	}
	
	@Transactional
	public Iterable<Movie> allMovie(){
		return this.movieRepository.findAllByOrderByTitle();
	}
	
	@Transactional
	public Movie findMovieById(Long id) {
		return this.movieRepository.findById(id).get();
	}
	
	@Transactional
	public void delMovieById(Long id) {
		this.movieRepository.delete(this.movieRepository.findById(id).get());
	}
	
	@Transactional
	public void saveMovie(Movie movie) {
		this.movieRepository.save(movie);
	}
	
	@Transactional
	public List<Movie> findMovieByYear(int year) {
		return this.movieRepository.findByYear(year);
	}
	
	@Transactional
	public List<Movie> findByTitle(String title){
		return this.movieRepository.findByTitleContainingIgnoreCase(title);
	}
	
	@Transactional
	public Movie changeMovieTitle(Long movieId, String title) {
		Movie movie = this.findMovieById(movieId);
		movie.setTitle(title);
		this.movieRepository.save(movie);
		return movie;
	}
	
	@Transactional
	public Movie changeMovieYear(Long id, Integer year) {
		Movie movie = this.findMovieById(id);
		movie.setYear(year);
		this.movieRepository.save(movie);
		return movie;
	}
	
	@Transactional
	public List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();
		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	
	

	
	



}
