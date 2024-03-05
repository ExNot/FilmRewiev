document.addEventListener("DOMContentLoaded", function () {

    const options = {
      method: 'GET',
      headers: {
        accept: 'application/json',
        Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ODlmMmM1NGFiY2NkOWIyNmZhODE1MTJjODcxN2M0OSIsInN1YiI6IjY1Yzc5ZDQ2YTkzZDI1MDE0OTRhYWNhNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.TSR3Va-v6otGV_hrkUTI0XUOhV28ljwIK380XxaQLR8'
      }
    };

    const filmListContainer = document.getElementById("filmList");

    fetch('https://api.themoviedb.org/3/movie/popular?language=en-US&page=1', options)
        .then(response => response.json())
        .then(data => {
            const films = data.results;

            films.forEach(film => {
                const filmCard = document.createElement("div");
                filmCard.classList.add("film-card");

                const filmPoster = document.createElement("img");

                filmPoster.src = 'https://image.tmdb.org/t/p/w500' + film.poster_path;
                filmPoster.alt = film.title;

                const filmTitle = document.createElement("p");
                filmTitle.textContent = film.title;

                filmCard.appendChild(filmPoster);
                filmCard.appendChild(filmTitle);
                console.log(filmTitle);
                filmListContainer.appendChild(filmCard);
            });
        })
        .catch(error => console.error('Error fetching films:', error));
});
