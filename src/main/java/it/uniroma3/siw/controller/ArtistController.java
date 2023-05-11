package it.uniroma3.siw.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;

@Controller
public class ArtistController {

	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired
	private ArtistValidator artistValidator;
	
	@Autowired
	private ImageRepository imageRepository;

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

			byte[] imageData = file.getBytes();
			String imageName = file.getOriginalFilename();

			Image image = new Image();
			image.setName(imageName);
			image.setBytes(imageData);


			artist.addImage(image);


			this.artistRepository.save(artist); 
			this.imageRepository.save(image);
			model.addAttribute("artist", artist);
			
			return "guest/artist.html";
		} else {
			return "admin/formNewArtist.html"; 
		}
	}

	@GetMapping("/guest/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "guest/artist.html";
	}

	@GetMapping("/guest/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "guest/artists.html";
	}
}
