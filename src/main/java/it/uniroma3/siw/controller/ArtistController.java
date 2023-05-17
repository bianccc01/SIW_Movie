package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class ArtistController {

	@Autowired
	private ArtistService artistService;

	@Autowired
	private ArtistValidator artistValidator;

	@Autowired 
	private CredentialsService credentialsService;

	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}

	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}

	@PostMapping("/admin/artist")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, @RequestParam("file") MultipartFile file, BindingResult bindingResult, Model model) throws IOException {
		this.artistValidator.validate(artist, bindingResult);
		if(!bindingResult.hasErrors()) {

			this.artistService.newImagesArtist(file, artist);

			this.artistService.saveArtist(artist);
			model.addAttribute("artist", artist);

			return "guest/artist.html";
		} else {
			return "admin/formNewArtist.html"; 
		}
	}

	@PostMapping("/guest/searchArtistName")
	public String searchMoviesName(Model model, @RequestParam String s) {
		model.addAttribute("artists", this.artistService.findArtistByNameAndSurname(s));
		return "guest/artists.html";
	}

	@GetMapping("/guest/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.findArtistById(id));
		return "guest/artist.html";
	}

	@GetMapping("/guest/artist")
	public String getArtists(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof AnonymousAuthenticationToken) {
			model.addAttribute("user", null);
		}

		else {

			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());


			model.addAttribute("user", credentials.getUser());
		}

		model.addAttribute("artists", this.artistService.allArtist());

		return "guest/artists.html";
	}
}
