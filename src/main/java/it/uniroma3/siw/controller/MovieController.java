package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.*;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.MovieService;

@Controller
public class MovieController {

	@Autowired 
	private MovieValidator movieValidator;

	@Autowired 
	private CredentialsService credentialsService;

	@Autowired
	private MovieService movieService;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private ImageService imageService;


	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieService.findMovieById(id));
		return "admin/formUpdateMovie.html";
	}

	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}

	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.allMovie());
		return "admin/manageMovies.html";
	}

	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {

		model.addAttribute("movie", this.movieService.setDirectorToMovie(directorId, movieId));
		return "admin/formUpdateMovie.html";
	}


	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistService.allArtist());
		model.addAttribute("movie", movieService.findMovieById(id));
		return "admin/directorsToAdd.html";
	}

	@GetMapping("/admin/delMovie/{id}")
	public String delMovie(@PathVariable("id") Long id, Model model) {

		this.movieService.delMovieById(id);
		model.addAttribute("movies", this.movieService.allMovie());

		return "admin/manageMovies.html";
	}

	@GetMapping("/admin/delMovieImage/{idImage}/{idMovie}")
	public String delMovieImage(@PathVariable("idImage") Long id, @PathVariable("idMovie") Long idMovie,  Model model) {

		this.imageService.delImage(id);
		model.addAttribute("movie", this.movieService.findMovieById(idMovie));

		return "admin/formUpdateMovie.html";
	}

	@GetMapping("/admin/changeMovieTitleForm/{idMovie}")
	public String changeMovieTitleForm(@PathVariable("idMovie") Long id,Model model) {

		model.addAttribute("movie",this.movieService.findMovieById(id));
		return "admin/changeMovieTitleForm.html";
	}


	@PostMapping("/admin/changeMovieTitle/{id}")
	public String changeMovieTitle(@PathVariable("id") Long id, @RequestParam("text") String title, Model model) {
		model.addAttribute("movie",this.movieService.changeMovieTitle(id, title));
		return "guest/movie.html";
	}

	@GetMapping("/admin/changeMovieYearForm/{id}")
	public String changeMovieYearForm(@PathVariable("id") Long id,Model model) {

		model.addAttribute("movie",this.movieService.findMovieById(id));
		return "admin/changeMovieYearForm.html";
	}

	@PostMapping("/admin/changeMovieYear/{id}")
	public String changeMovieYear(@PathVariable("id") Long id, @RequestParam("text") Integer year, Model model) {
		model.addAttribute("movie",this.movieService.changeMovieYear(id, year));
		return "guest/movie.html";
	}

	@PostMapping("/admin/addImageMovie/{id}")
	public String addImageMovie(@PathVariable("id") Long id, @RequestParam("file") MultipartFile[] files, Model model) throws IOException {

		Movie movie = this.movieService.findMovieById(id);
		this.movieService.newImagesMovie(files, movie);
		model.addAttribute("movie",movie);

		return "admin/formUpdateMovie.html";
	}



	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, 
			@RequestParam("file") MultipartFile[] files, Model model) throws IOException {

		this.movieValidator.validate(movie, bindingResult);

		if (!bindingResult.hasErrors()) {

			this.movieService.saveMovie(movie);
			this.movieService.newImagesMovie(files, movie);

			model.addAttribute("movie", movie);
			return "guest/movie.html";

		} else {
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/guest/movie/{id}/{idImage}")
	public String getMovie(@PathVariable("id") Long id, @PathVariable("idImage") int idImage, Model model) {

		Movie movie = this.movieService.findMovieById(id);
		model.addAttribute("movie", movie);

		Image image = this.movieService.getImageMovie(movie, idImage);
		
		model.addAttribute("image",image);
		model.addAttribute("id",movie.getImages().indexOf(image));

		return "guest/movie.html";
	}

	@GetMapping("/guest/movie")
	public String getMovies(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof AnonymousAuthenticationToken) {
			model.addAttribute("user", null);
		}

		else {

			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());


			model.addAttribute("user", credentials.getUser());
		}

		model.addAttribute("movies", this.movieService.allMovie());

		return "guest/movies.html";
	}



	@GetMapping("/guest/formSearchMovies")
	public String formSearchMovies() {
		return "guest/formSearchMovies.html";
	}

	@PostMapping("/guest/searchMovies")
	public String searchMovies(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieService.findMovieByYear(year));
		return "guest/foundMovies.html";
	}


	@PostMapping("/guest/searchMoviesName")
	public String searchMoviesName(Model model, @RequestParam String title) {
		model.addAttribute("movies", this.movieService.findByTitle(title));
		return "guest/movies.html";
	}

	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		List<Artist> actorsToAdd = this.movieService.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieService.findMovieById(id));

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		this.movieService.saveMovie(movie);

		List<Artist> actorsToAdd = this.movieService.actorsToAdd(movieId);

		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		this.movieService.saveMovie(movie);

		List<Artist> actorsToAdd = this.movieService.actorsToAdd(movieId);

		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}




}
