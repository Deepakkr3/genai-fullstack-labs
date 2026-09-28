package com.deepak.genAI;


import com.deepak.genAI.DTO.MovieSearchResponse;
import com.deepak.genAI.model.Movie;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
@CrossOrigin(origins = "*")
public class MovieController {

    final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @GetMapping("/search")
    public void search(@RequestParam  String query){

        movieService.movieSearch(query);

    }



    @GetMapping("/search/new")
    public List<MovieSearchResponse> searchOld(
            @RequestParam String query
    ) {

        return movieService.movieSearch2(query);
    }

    @GetMapping("/count")
    public String count() {

        return "Movies loaded: "
                + movieService.getMovieCount();
    }


}
