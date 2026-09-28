package com.deepak.genAI;


import com.deepak.genAI.DTO.MovieSearchResponse;
import com.deepak.genAI.model.Movie;
import com.deepak.genAI.model.MovieData;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

@Service
public class MovieService {


    private final EmbeddingModel embeddingModel;
//    private List<Movie> movieEmbedding=new ArrayList<>();
    private final ObjectMapper objectMapper;
    private final List<Movie> movieEmbeddings = new ArrayList<>();

    public MovieService(EmbeddingModel embeddingModel,ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
        this.embeddingModel=embeddingModel;
    }

    public void movieSearch(String query){
        float[] embedding=embeddingModel.embed(query);
        System.out.println("length of embedding is : "+embedding.length);
        System.out.println("embeddin : "+ Arrays.toString(embedding));
    }

   @PostConstruct
    public void createMovieEmbeddings() {

        try {

            // Load movies.json
            InputStream inputStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream("movieData.json");

            if (inputStream == null) {
                throw new RuntimeException("movies.json not found");
            }

            // Convert JSON -> List<MovieData>
            List<Movie> movies =
                    objectMapper.readValue(
                            inputStream,
                            new TypeReference<List<Movie>>() {
                            }
                    );

            System.out.println("Movies loaded: " + movies.size());

            // Generate embedding for every movie
            for (Movie movieData : movies) {

                String text = """
                        Title: %s
                        Description: %s
                        """.formatted(
                        movieData.getTitle(),
                        movieData.getDescription()
                );

                float[] embedding = embeddingModel.embed(text);

                Movie movie = new Movie(
                        movieData.getTitle(),
                        movieData.getDescription(),
                        embedding
                );

                movieEmbeddings.add(movie);

                System.out.println(
                        "Embedded: " + movieData.getTitle()
                );
            }

            System.out.println(
                    "Total movie embeddings: "
                            + movieEmbeddings.size()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create movie embeddings",
                    e
            );
        }
    }

    public List<MovieSearchResponse> movieSearch2(String query) {

        // Create embedding for user query
        float[] queryEmbedding = embeddingModel.embed(query);

        System.out.println(
                "Query embedding length: " + queryEmbedding.length
        );

        return movieEmbeddings.stream()
                .map(movie -> {

                    double similarity = cosineSimilarity(
                            queryEmbedding,
                            movie.getEmbedding()
                    );

                    return new MovieSearchResponse(
                            movie.getTitle(),
                            movie.getDescription(),
                            similarity
                    );
                })
                .sorted(
                        Comparator.comparingDouble(
                                MovieSearchResponse::similarity
                        ).reversed()
                )
                .limit(2)
                .toList();
    }

    private double cosineSimilarity(
            float[] vectorA,
            float[] vectorB
    ) {

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {

            dotProduct +=
                    vectorA[i] * vectorB[i];

            normA +=
                    vectorA[i] * vectorA[i];

            normB +=
                    vectorB[i] * vectorB[i];
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct /
                (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /*
     * Optional method for checking loaded movies
     */
    public int getMovieCount() {
        return movieEmbeddings.size();
    }
}
