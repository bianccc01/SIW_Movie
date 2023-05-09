package it.uniroma3.siw.model;

import java.util.Base64;

import javax.persistence.*;
import javax.transaction.Transactional;

@Entity
public class Image {
	
	   @Id
	   @GeneratedValue(strategy = GenerationType.AUTO)
	   private Long id;
	   
	   private String name;
	   
	   @Lob
	   private byte[] bytes;
	   
	   
	   @Lob
	   private String base64Image;
	   
	   @ManyToOne
	   private Movie movie;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	@Transactional
	public String getBase64Image() {
		return base64Image;
	}
	
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}
	   
	   
	
	

}
