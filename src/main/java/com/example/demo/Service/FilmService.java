package com.example.demo.Service;

import com.example.demo.Model.Film;
import com.example.demo.Model.Relation.UserRating;
import com.example.demo.Repository.FilmRepository;
import com.example.demo.Repository.UserRatingRepository;
import com.example.demo.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
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
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserRatingRepository userRatingRepository;


    public FilmService(FilmRepository filmRepository, UserRepository userRepository, UserRatingRepository userRatingRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.userRatingRepository = userRatingRepository;
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




    private Document fetchIMDBDocument(String filmTitle) throws IOException {
        String url = "https://www.imdb.com/find/?s==tt&q=" + filmTitle + "&ref=nv%20srsm";
        return Jsoup.connect(url).timeout(10 * 1000).get();
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

        return convertJsonToFilmList(response.body());
    }

    private List<Film> convertJsonToFilmList(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode =objectMapper.readTree(json);

        List<Film> filmList = new ArrayList<>();

        if (jsonNode.has("results") && jsonNode.get("results").isArray()){
            ArrayNode resultsArray = (ArrayNode) jsonNode.get("results");

            for (JsonNode filmNode : resultsArray){
                Film film = new Film();

                if (filmNode.path("title").asText().equals("See company contact information")){

                }

                film.setId(filmNode.path("id").asLong());
                film.setName(filmNode.path("title").asText());
                film.setDirector(fetchDirectorFromAPI(filmNode.path("id").asInt()));
                film.setReleaseDate(Year.of(Integer.parseInt(filmNode.path("release_date").asText().substring(0,4))));
                film.setIMDBRating(fetchRatingFromIMDB(filmNode.path("title").asText()));
                film.setDescription(filmNode.path("overview").asText());
                film.setImgUrl("https://image.tmdb.org/t/p/w500" + filmNode.path("poster_path").asText());
                filmList.add(film);
                System.out.println("https://image.tmdb.org/t/p/w500" + filmNode.path("poster_path").asText());
            }
        }
        return filmList;
    }
    /*private String fetchDirectorFromIMDB(String filmTitle) throws IOException {
        try {
            Document document = fetchIMDBDocument(filmTitle);

            Element filmLink = document.select("a.ipc-metadata-list-summary-item__t").first();
            if (filmLink != null) {
                String filmUrl = "https://www.imdb.com" + filmLink.attr("href");
                Document filmDocument = Jsoup.connect(filmUrl).timeout(10 * 1000).get();

                Element directorElement = filmDocument.select("a.ipc-metadata-list-item__list-content-item").first();

                if (directorElement != null) {
                    return directorElement.text();
                }
            }
        }catch (SocketTimeoutException e){
            e.printStackTrace();
            return null;
        }


        return null;
    }*/
    private String fetchDirectorFromAPI(int movieId) throws IOException {
        try {

            String apiKey = "789f2c54abccd9b26fa81512c8717c49";

            String apiUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + apiKey;
            URL url = new URL(apiUrl);

            // Creating the HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Reading the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();


                JsonElement jsonElement = JsonParser.parseString(response.toString());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonArray crewArray = jsonObject.getAsJsonArray("crew");



                for (JsonElement crewMember : crewArray) {
                    JsonObject crewObject = crewMember.getAsJsonObject();
                    String job = crewObject.get("job").getAsString();

                    if ("Director".equals(job)) {
                        String directorName = crewObject.get("name").getAsString();
                        return directorName;
                    }
                }
            } else {
                System.out.println("Error: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Double fetchRatingFromIMDB(String filmTitle) throws IOException {

        try {
            Document document = fetchIMDBDocument(filmTitle);

            Element filmLink = document.select("a.ipc-metadata-list-summary-item__t").first();
            if (filmLink != null) {
                String filmUrl = "https://www.imdb.com" + filmLink.attr("href");
                Document filmDocument = Jsoup.connect(filmUrl).timeout(10 * 1000).get();

                Element ratingElement = filmDocument.select("span.sc-bde20123-1.cMEQkK").first();
                if (ratingElement != null) {
                    return Double.parseDouble(ratingElement.text());
                }
            }

            return null;
        }catch (SocketTimeoutException e){
            e.printStackTrace();
            return null;
        }

    }
    @Transactional
    public void rateFilm(Long filmId, double rating){
        Optional<Film> optionalFilm = filmRepository.findById(filmId);
        if (optionalFilm.isPresent()){
            Film film = optionalFilm.get();

            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserRating existingRating = userRatingRepository.findByUser_IdAndFilm_Id(customUserDetails.getId(), filmId);
            if (existingRating != null){
                existingRating.setRating(rating);
                userRatingRepository.save(existingRating);
            } else{
                UserRating ratingObj = new UserRating();
                ratingObj.setUser(userRepository.findByUsername(customUserDetails.getUsername()).get());
                ratingObj.setFilm(film);
                ratingObj.setRating(rating);
                userRatingRepository.save(ratingObj);
            }

        }

    }






}

















