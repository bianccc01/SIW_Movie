package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	private ImageRepository imageRepository;

	@Transactional
	public Iterable<Artist> allArtist(){
		return this.artistRepository.findAll();
	}

	@Transactional
	public Artist findArtistById(Long id) {
		return this.artistRepository.findById(id).get();
	}

	@Transactional
	public void saveArtist(Artist artist) {
		this.artistRepository.save(artist);
	}

	@Transactional
	public void newImagesArtist(MultipartFile file, Artist artist) throws IOException {

		byte[] imageData = file.getBytes();
		String imageName = file.getOriginalFilename();

		Image image = new Image();
		image.setName(imageName);
		image.setBytes(imageData);


		artist.addImage(image);
		this.imageRepository.save(image);
	}
	
	@Transactional
	public List<Artist> findArtistByNameAndSurname(String s){
		List<Artist> artists = new ArrayList<>();
		artists.addAll(this.artistRepository.findByNameContainingIgnoreCase(s));
		artists.addAll(this.artistRepository.findBySurnameContainingIgnoreCase(s));
		return artists.stream().distinct().collect(Collectors.toList());
	}

}
