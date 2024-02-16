package com.example.demo.Service;

import com.example.demo.Model.Film;
import com.example.demo.Repository.FilmRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FilmService {
    @Autowired
    private final FilmRepository filmRepository;


    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public List<Film> getAllFilms(){
        return filmRepository.findAll();
    }
    public Optional<Film> getFilmById(Long id){
        return filmRepository.findById(id);
    }

    public Film saveFilm(Film film){
        return filmRepository.save(film);
    }
    public void deleteFilm(Long id){
        filmRepository.deleteById(id);
    }




    public void fetchAndAddMovies() throws IOException, InterruptedException {
        List<Film> filmsFromApi = fetchFilmFromApi();
        List<Film> existingFilms = filmRepository.findAll();
        for (Film film : filmsFromApi){
            boolean existInDatabase = existingFilms.stream()
                            .anyMatch(existingFilm -> existingFilm.getName().equals(film.getName()));
            if (!existInDatabase){
                filmRepository.save(film);
            }
        }
    }

    private List<Film> fetchFilmFromApi() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1"))
                .header("accept", "application/json")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ODlmMmM1NGFiY2NkOWIyNmZhODE1MTJjODcxN2M0OSIsInN1YiI6IjY1Yzc5ZDQ2YTkzZDI1MDE0OTRhYWNhNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.TSR3Va-v6otGV_hrkUTI0XUOhV28ljwIK380XxaQLR8")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return convertJsonToFilmDTOList(response.body());
    }

    private List<Film> convertJsonToFilmDTOList(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode =objectMapper.readTree(json);

        List<Film> filmList = new ArrayList<>();

        if (jsonNode.has("results") && jsonNode.get("results").isArray()){
            ArrayNode resultsArray = (ArrayNode) jsonNode.get("results");

            for (JsonNode filmNode : resultsArray){
                Film film = new Film();

                film.setId(filmNode.path("id").asLong());
                film.setName(filmNode.path("title").asText());
                film.setDirector(fetchDirectorFromIMDB(filmNode.path("title").asText()));
                film.setReleaseDate(Year.of(Integer.parseInt(filmNode.path("release_date").asText().substring(0,4))));
                film.setDescription(filmNode.path("overview").asText());
                filmList.add(film);
            }
        }
        return filmList;
    }

    private String fetchDirectorFromIMDB(String filmTitle) throws IOException {
        String url = "https://www.imdb.com/find/?s==tt&q=" + filmTitle + "&ref=nv%20srsm";
        Document document = Jsoup.connect(url).get();

        Element filmLink = document.select("a.ipc-metadata-list-summary-item__t").first();
        if (filmLink !=null){
            String  filmUrl ="https://www.imdb.com" + filmLink.attr("href");
            Document filmDocument =  Jsoup.connect(filmUrl).get();

            Element directorElement =filmDocument.select("a.ipc-metadata-list-item__list-content-item").first();
            if (directorElement != null){
                return directorElement.text();
            }
        }
            return null;
    }
}
















