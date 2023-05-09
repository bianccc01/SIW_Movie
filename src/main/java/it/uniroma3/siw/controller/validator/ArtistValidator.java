package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;


@Component
public class ArtistValidator implements Validator {

	@Autowired
	private ArtistRepository artistRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Artist artist = (Artist)o;
		if (artist.getName()!=null && artist.getSurname()!=null
				&& artistRepository.existsByNameAndSurname(artist.getName(),artist.getSurname())) {
			errors.reject("artist.duplicate");
		}

		if (artist.getDateOfDeath()!= null && artist.getDateOfBirth().compareTo(artist.getDateOfDeath()) > 0) {
			errors.reject("artist.wrongDeathDate");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Artist.class.equals(aClass);
	}
}
