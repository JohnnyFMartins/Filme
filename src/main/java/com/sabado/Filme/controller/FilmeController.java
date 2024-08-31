package com.sabado.Filme.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sabado.Filme.model.Filme;
import com.sabado.Filme.repo.FilmeRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/filme")
public class FilmeController {
	
	@Autowired
	private FilmeRepository FilmeRepo;
	
	private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/";
	
	// http://localhost:8080/filme/
	@GetMapping("/")
	public String inicio(Model model) { // model -> org.springframe...
		
		model.addAttribute("filmes", FilmeRepo.findAll());
		
		return "index"; // SELECT * FROM filmes;
	}

	// http://localhost:8080/filme/form
	@GetMapping("/form")
	public String form(Model model) {
		model.addAttribute("filme", new Filme());
		return "form";
	}
	// http://localhost:8080/filme/form/778 -> id é o 778
	@GetMapping("/form/{id}")
	public String form(@PathVariable("id")Long id, Model model) {
		Optional<Filme> filme = FilmeRepo.findById(id);
		if (filme.isPresent()) {
			model.addAttribute("filme", filme.get());
		} else {
			model.addAttribute("filme", new Filme());
		}
		return "form";
	}

	@PostMapping("/add")
	public String addFilme(@RequestParam("id") Optional<Long> id,
			@RequestParam("nome") String nome,
			@RequestParam("data") String data,
			@RequestParam("imagem") MultipartFile imagem) {
		
		Filme filme;
		if(id.isPresent()) {
			filme = FilmeRepo.findById(id.get()).orElse(new Filme());
		} else {
			filme = new Filme();
		}
		filme.setNome(nome);
		filme.setData(Date.valueOf(data)); // salvar dentro do banco de dados
		FilmeRepo.save(filme);
		// salvar imagem
		// ! -> indica diferente
		if(!imagem.isEmpty()) {
			try {
				// Lógica para salvar a imagem
				// filme_32_imagemthor.png
				String fileName = "filme_" + filme.getId() + "_" + imagem.getOriginalFilename();
				// java.nio.file
				Path path = Paths.get(UPLOAD_DIR + fileName); // Capturando caminho completo
				Files.write(path, imagem.getBytes()); // Escrevendo a Imagem
				filme.setImagem("/" + fileName); // Adicionar o caminho para acessar a imagem
				FilmeRepo.save(filme); // Salvar a imagem
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "redirect:/filme/";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteFilme(@PathVariable("id") Long id) {
		Optional<Filme>filme = FilmeRepo.findById(id);
		
		if(filme.isPresent()) {
			Filme filmeParaDeletar = filme.get();
			String imagePath = UPLOAD_DIR + filmeParaDeletar.getImagem();
			try {
				Files.deleteIfExists(Paths.get(imagePath));
			} catch (Exception e) {
				e.printStackTrace();
			}
			FilmeRepo.deleteById(id);
		}
		
		return "redirect:/filme/";
		
	}
	
}
