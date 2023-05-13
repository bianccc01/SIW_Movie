package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;

@Service
public class MovieService {
	

	
	
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
	}
	
	

}
